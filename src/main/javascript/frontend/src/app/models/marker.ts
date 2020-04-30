import { MarkerType } from './marker-type.enum';
import { Hit } from './hit';
import { Dimension } from './dimension.enum';
import { Point } from './point';
import { v4 as uuid } from 'uuid';

export class Marker {
  readonly _id: string = uuid();
  readonly dimension?: Dimension;
  readonly trackId?: number;
  private _hits = [] as Hit[];
  private x = [] as number[];
  private y = [] as number[];
  private mode: string;
  private hoverinfo: string;
  private type = 'scattergl';
  private marker = {
    symbol: 'circle',
    color: 'rgb(0, 255, 0)',
    size: 7,
    line: {
      color: `rgb(0,0,0)`,
      width: 0.5
    }
  };

  constructor(readonly markerType: MarkerType, options: MarkerOptions) {
    switch (this.markerType) {
      case MarkerType.DIMENSION:
        this.mode = 'markers';
        this.hoverinfo = 'text+x+y';
        this.marker.symbol = 'diamond';
        break;
      case MarkerType.TRACE:
        this.mode = 'lines';
        this.hoverinfo = 'skip';
        this.marker.line = undefined;
        break;
    }

    if (options != null) {
      this.dimension = options.dimension;
      this.trackId = options.trackId;

      if (options.color != null) {
        this.changeColor(options.color);
      }
    }
  }

  public changeColor(color: string) {
    this.marker.color = color;
  }

  public get(trackId: number): Hit {
    const index = this.getIndex(trackId);
    return index === -1 ? null : this._hits[index];
  }

  public getIndex(trackId: number): number {
    return (trackId == null || this.markerType !== MarkerType.DIMENSION) ? -1
      : this._hits.findIndex(value => value.trackId === trackId);
  }

  public getHitByIndex(index: number): Hit {
    return this._hits[index];
  }

  public put(hit: Hit, front?: boolean) {
    const index = this.getIndex(hit.trackId);
    if (index === -1) {
      // no existing hit with this track id exists
      if (front == null || !front) {
        // add to end of list
        this._hits.push(hit);
        this.update(this._hits.length - 1, 0);
      } else {
        // add to front of list
        this._hits.unshift(hit);
        this.update(0, 1);
      }
    } else {
      // update existing hit at the given index
      this._hits[index] = hit;
      this.update(index);
    }
  }

  public update(index: number, mode?: number) {
    const hit = this._hits[index];
    const p = this.overworldCoord(hit);
    switch (mode) {
      case 0:
        this.x.push(p.x);
        this.y.push(p.z);
        break;
      case 1:
        this.x.unshift(p.x);
        this.y.unshift(p.z);
        break;
      case 2:
      default:
        this.x[index] = p.x;
        this.y[index] = p.z;
    }
    this._hits[index]._updated = true;
  }

  public needsUpdate() {
    this._hits.forEach(hit => hit._updated = false);
  }

  public remove(index: number) {
    this._hits.splice(index, 1);
    this.x.splice(index, 1);
    this.y.splice(index, 1);
  }

  public removeHit(hit: Hit) {
    this.remove(this.getIndex(hit.trackId));
  }

  public clear() {
    this._hits.length = 0;
    this.x.length = 0;
    this.y.length = 0;
  }

  private overworldCoord(hit: Hit): Point {
    const dim = hit.dimension != null ? hit.dimension : (this.dimension != null ? this.dimension : Dimension.OVERWORLD);
    const inNether = dim === Dimension.NETHER;
    return {
      x: inNether ? hit.x * 8 : hit.x,
      z: inNether ? hit.z * 8 : hit.z
    };
  }
}

export interface MarkerOptions {
  dimension?: Dimension;
  trackId?: number;
  color?: string;
}

export type DimensionColorMap = {
  [dimension in Dimension]: string | string[];
};
