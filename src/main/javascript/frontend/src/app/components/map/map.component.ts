import { Component, OnDestroy, OnInit } from '@angular/core';
import { PlotlyService } from 'angular-plotly.js';
import { Track } from '../../models/track';
import { DimensionColorMap, Marker } from '../../models/marker';
import { MarkerType } from '../../models/marker-type.enum';
import { Dimension, fixDimensionMember } from '../../models/dimension.enum';
import { environment } from '../../../environments/environment';
import { AuthenticationService } from '../../services/authentication/authentication.service';
import * as SockJS from 'sockjs-client';
import { Client, StompSubscription } from '@stomp/stompjs';
import { Plotly } from 'angular-plotly.js/src/app/shared/plotly.interface';
import { of } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { Hit } from '../../models/hit';
import { HttpClient } from '@angular/common/http';
import { TrackHistory } from '../../models/track-history';
import PlotlyHTMLElement = Plotly.PlotlyHTMLElement;

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit, OnDestroy {
  public data = [] as Marker[];
  public layout = {
    x: 0,
    y: 0,
    width: 0,
    height: 0,
    margin: {
      l: 0,
      r: 0,
      t: 0,
      b: 0
    },
    showlegend: false,
    hovermode: 'closest',
    dragmode: 'pan',
    yaxis: {
      scaleanchor: 'x',
      scaleratio: 1.0
    }
  };
  public config = {
    scrollZoom: true
  };
  public revision = 0;

  private client: Client;
  private subscriptions: StompSubscription[] = [];
  private trackLock: boolean;

  private traceColors: DimensionColorMap = {
    [Dimension.NETHER]: [
        `rgb(255, 111, 0)`,
        `rgb(245, 197, 54)`
    ],
    [Dimension.OVERWORLD]: [
      'rgb(0,100,0)',
      'rgb(124,252,0)'
    ],
    [Dimension.END]: [
      'rgb(25,25,112)',
      'rgb(2,253,217)'
    ]
  };

  constructor(private plotly: PlotlyService, private auth: AuthenticationService, private http: HttpClient) {
    this.tailMarker(new Marker(MarkerType.DIMENSION, {dimension: Dimension.NETHER, color: `rgb(255,0,0)`}));
    this.tailMarker(new Marker(MarkerType.DIMENSION, {dimension: Dimension.OVERWORLD, color: `rgb(0,255,0)`}));
    this.tailMarker(new Marker(MarkerType.DIMENSION, {dimension: Dimension.END, color: `rgb(0,0,255)`}));
  }

  ngOnInit(): void {
    this.client = new Client({
      webSocketFactory: () => new SockJS(`${environment.apiUrl}/websocket?accessToken=${this.auth.user.accessToken}`)
    });

    this.client.onConnect = receipt => {
      const sub = this.client.subscribe('/ws-user/ws-subscribe/tracker',
        message => of(message.body)
          .pipe(
            // parse response body and cast it to a track model
            map(body => JSON.parse(body) as Track[]),
            // fix track data having wrong types
            tap(tracks => tracks.forEach(track => fixDimensionMember(track)))
          ).subscribe({
            next: tracks => this.onTrackUpdate(tracks),
            error: err => console.error('failed to update tracks', err)
          }));
      this.subscriptions.push(sub);

      setInterval(() => this.onTick(), 500);
    };

    // reset
    this.trackLock = false;

    this.client.activate();
  }

  ngOnDestroy(): void {
    // unsub from all listeners
    while (this.subscriptions.length > 0) {
      this.subscriptions.pop().unsubscribe();
    }
    // disconnect from websocket
    this.client.deactivate();
  }

  onPlotInit() {
    this.onResize();
    this.onTick();
  }

  onTick() {
    if (!this.client.connected) {
      return;
    }

    this.client.publish({
      destination: '/ws-api/tracking',
      body: JSON.stringify({
        server: '2b2t.org',
        duration: 10000
      })
    });
  }

  onTrackUpdate(tracks: Track[]) {
    // clear all markers
    this.getDimensionMarkers().forEach(marker => marker.clear());

    tracks.forEach(track => {
      const marker = this.getMarkerByDimension(track.dimension);
      marker.put(track);

      const traceMarker = this.getMarkersByType(MarkerType.TRACE)
        .find(m => m.trackId === track.trackId);
      if (traceMarker != null) {
        traceMarker.put(track, true);
      }
    });

    this.redrawGraph();
  }

  onTrackHistoryUpdate(history: TrackHistory[]) {
    // clear previous markers
    this.removeTraceMarkers();

    let lastHit: Hit = null;

    history
      .filter(track => track.hits.length > 0)
      .forEach((track, index) => {
        const colors = this.traceColors[track.dimension];
        const marker = new Marker(MarkerType.TRACE, {
          trackId: track.trackId,
          dimension: track.dimension,
          color: colors[index % colors.length]
        });

        track.hits.forEach(hit => marker.put(hit));

        if (lastHit != null) {
          marker.put(lastHit, true);
        }

        lastHit = track.hits[track.hits.length - 1];
        lastHit.dimension = track.dimension;
        lastHit.trackId = track.trackId;

        // add this marker to the front of the list
        this.headMarker(marker);
      });

    this.redrawGraph();
  }

  onClicked(data: any) {
    if (this.trackLock) {
      console.warn('Currently getting history for another track');
      return;
    }

    const point = data.points[0];
    const index = point.pointIndex;
    const marker: Marker = point.data;

    if (marker.markerType !== MarkerType.DIMENSION) {
      console.error('Can only get track history for DIMENSION markers');
      return;
    }

    const hit: Hit = marker.getHitByIndex(index);

    if (hit == null) {
      console.error(`Index ${index} points to an invalid hit`);
      return;
    }

    this.trackLock = true;
    this.http.post(`${environment.apiUrl}/api/full-track-history`, null, {
      params: {
        trackId: hit.trackId.toString(),
        max: '0',
        aggregationMs: '10000'
      }
    }).pipe(
      map(response => response as TrackHistory[]),
      tap(history => history.forEach(track => fixDimensionMember(track)))
    ).subscribe({
      next: history => this.onTrackHistoryUpdate(history),
      error: err => console.error('failed to update track history', err),
      complete: () => this.trackLock = false
    });
  }

  onResize() {
    const overview = this.getGraphDiv();
    this.layout.width = overview.offsetWidth;
    this.layout.height = (window.innerHeight - overview.offsetTop);
  }

  private redrawGraph() {
    this.revision++;
    // this.getPlotly().react(this.getGraphDiv(), [].concat(this.data), this.layout, this.config);
    this.getPlotly().redraw(this.getGraphDiv());
  }

  private headMarker(marker: Marker) {
    this.data.unshift(marker);
  }

  private tailMarker(marker: Marker) {
    this.data.push(marker);
  }

  private getGraphDiv(): PlotlyHTMLElement {
    return this.plotly.getInstanceByDivId('overview');
  }

  private getPlotly(): any {
    return this.plotly.getPlotly();
  }

  private getMarkerById(id: string) {
    return this.data.find(value => value._id === id);
  }

  private getMarkersByType(type: MarkerType): Marker[] {
    return this.data.filter(value => value.markerType === type);
  }

  private getDimensionMarkers(): Marker[] {
    return this.getMarkersByType(MarkerType.DIMENSION);
  }

  private getMarkerByDimension(dimension: Dimension | string): Marker {
    return this.getDimensionMarkers().find(value => value.dimension === dimension);
  }

  private removeTraceMarkers() {
    this.data = this.data.filter(marker => marker.markerType !== MarkerType.TRACE);
  }
}
