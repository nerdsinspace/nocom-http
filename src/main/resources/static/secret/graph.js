const j_server = $('#server');
const j_dimension = $('#dimension');
const j_range = $('#range');
const j_delta = $('#delta');
const j_hits = $('#hits');
const j_start_date = $('#startDate');
const j_end_date = $('#endDate');
const j_submit = $('#submit');

const j_coord_selected = $('#coord-selected');

const settings = [j_server, j_dimension, j_range,
  j_delta, j_hits, j_start_date, j_end_date];

const j_map = $('#map');

const BUFFER_SIZE = 300;

const createFixedArray = () => {
  return new Array(BUFFER_SIZE).fill(0);
}

let markersOverworld = {
  _marker_type: 'dim',
  _track_id: [],
  x: [],
  y: [],
  // opacity: createFixedArray(),
  mode: 'markers',
  hoverinfo: "text+x+y",
  type: 'scattergl',
  marker: {
    color: 'rgb(0,255,0)',
    size: 7,
  },
};
let markersNether = {
  _marker_type: 'dim',
  _track_id: [],
  x: [],
  y: [],
  // opacity: createFixedArray(),
  mode: 'markers',
  hoverinfo: "text+x+y",
  type: 'scattergl',
  marker: {
    color: 'rgb(255,0,0)',
    size: 7,
  },
};

const netherTraceColors = [
    'rgb(255,111,0)',
    'rgb(245,197,54)'
];

const overworldTraceColors = [
  'rgb(0,34,255)',
  'rgb(2,253,217)'
];

const markersByDimension = [markersOverworld, markersNether];
const markersByHistory = [];
let markers = markersByDimension;

const plotLayout = {
  x: 0,
  y: 0,
  width: 0,
  height: 0,
  margin: {
    l: 0,
    r: 0,
    t: 0,
    b: 0,
  },
  showlegend: false,
  hovermode: 'closest',
  dragmode: 'pan',
  yaxis: {
    scaleanchor: "x",
    scaleratio: 1.0,
  },
};
const plotConfig = {
  scrollZoom: true,
};

function createHistoryMarker() {
  return {
    _marker_type: 'history',
    _current_track_id: 0,
    x: [],
    y: [],
    mode: 'lines',
    hoverinfo: 'skip',
    type: 'scattergl',
    marker: {},
  };
}

let isUpdatingTrackHistory = false;

async function _ajax(data, errorHandler) {
  try {
    return await $.ajax(data);
  } catch (e) {
    if (typeof errorHandler !== 'undefined') {
      errorHandler(e);
    }
  }
}

function graphInitialize() {
  // clear selection
  setCoordSelected();

  // clear markers array
  // markers.length = 0;

  Plotly.newPlot(j_map[0], markers, plotLayout, plotConfig).then(graphMouseListener);

  j_map[0].on('plotly_click', function (data) {
    console.trace();
    if(isUpdatingTrackHistory) {
      return;
    }

    const point = data.points[0];
    const index = point.pointIndex;

    if(point.data._marker_type === 'history') {
      console.warn('cannot track this marker');
      return;
    }

    const trackId = point.data._track_id[index];

    if(trackId === undefined || trackId === 0) {
      console.warn("unknown track id", trackId);
      return;
    }

    markers = markersByDimension;
    graphUpdate();

    setCoordSelected(`${point.x} ${point.y}`);

    isUpdatingTrackHistory = true;
    _ajax({
      type: 'POST',
      url: `/api/full-track-history`,
      data: {
        trackId: trackId,
        max: 0,
        aggregationMs: 10000
      },
      error: function (e) {
        console.warn("error getting track history", e)
      }
    }).then(data => {
      console.log(data);

      markersByHistory.length = 0;

      let lastX = undefined, lastY = undefined;

      data.forEach(function(track, index) {
        const mark = createHistoryMarker();
        mark._current_track_id = track.trackId;

        const nether = track.dimension === 'NETHER';
        mark.marker.color = !nether
            ? overworldTraceColors[index % 2]
            : netherTraceColors[index % 2];

        mark.x = track.hits.map(hit => nether ? hit.x * 8 : hit.x);
        mark.y = track.hits.map(hit => nether ? hit.z * 8 : hit.z);

        if(lastX !== undefined) {
          mark.x.unshift(lastX);
          mark.y.unshift(lastY);
        }

        if(mark.x.length > 0) {
          lastX = mark.x[mark.x.length - 1];
          lastY = mark.y[mark.y.length - 1];
          markersByHistory.push(mark);
        }
      });

      markers = markersByHistory.reverse().concat(markersByDimension);
      graphUpdate();
    }).finally(() => isUpdatingTrackHistory = false);

    // setCoordSelected(`${point.x} ${point.y}`);
    // setSelectedMarker(point.x, point.y);
  });
}

function graphMouseListener() {
  // j_map[0].addEventListener('mousemove', function(event) {
  //   const gd = j_map[0];
  //   const xaxis = gd._fullLayout.xaxis;
  //   const yaxis = gd._fullLayout.yaxis;
  //   const left = gd._fullLayout.margin.l;
  //   const top = gd._fullLayout.margin.t;
  //   const x = xaxis.p2c(event.x - left);
  //   const y = yaxis.p2c(event.y - top);
  // }); // TODO: shitz
}

function graphResize() {
  plotLayout.width = (j_map.width());
  plotLayout.height = (window.innerHeight - j_map.position().top);

  graphUpdate();
  graphRecenter();
}

function graphUpdate() {
  Plotly.react(j_map[0], markers, plotLayout, plotConfig);
}

function graphRecenter() {
  Plotly.relayout(j_map[0], {
    'xaxis.autorange': true,
    'yaxis.autorange': true
  });
}

function queryOptions() {
  const start = j_start_date.val(), end = j_end_date.val();
  return {
    server: j_server.val(),
    dimension: parseInt(j_dimension.val(), 10),
    groupingRange: parseInt(j_range.val(), 10),
    minDelta: parseInt(j_delta.val(), 10),
    minHits: parseInt(j_hits.val(), 10),
    startTime: isValid(start) ? toTimeMs(start) : undefined,
    endTime: isValid(end) ? toTimeMs(end) : undefined,
  }
}

function isValid(o) {
  return typeof o !== 'undefined' && o !== '';
}

function toTimeString(ms) {
  return new Date(ms).toLocaleString();
}

function toTimeMs(date) {
  return new Date(date).getTime();
}

function setCoordSelected(text) {
  j_coord_selected.val(text);
  if (typeof text == 'undefined' || text === '') {
    j_coord_selected.removeAttr('original');
  } else {
    j_coord_selected.attr('original', text);
  }
}

function setSelectedMarker(x, z) {
  const m = markers.find(e => e.id === 'selector');
  const data = {
    id: 'selector',
    x: [x],
    y: [z],
    mode: 'markers',
    type: 'scattergl',
    hoverinfo: 'skip',
    marker: {
      color: 'rgb(153,50,204)',
      size: 14,
    },
  };

  if (typeof m === 'undefined') {
    markers.splice(0, 0, data);
  } else {
    m.x = data.x;
    m.y = data.y;
  }

  graphUpdate();
}

function onSubmit(o) {
  // const $this = $(o);
  // $this.attr('disabled', true);
  // $this.tooltip('hide');
  //
  // // hide settings
  // $('#settings-collapse').collapse('hide');
  //
  // // reinitialize the graph
  // graphInitialize();
  //
  // const onFinish = (e) => $this.attr('disabled', false);
  //
  // try {
  //   return getLocations(queryOptions())
  //       .then(graphMarkers)
  //       .finally(onFinish);
  // } catch (e) {
  //   onFinish(e);
  // }
}

function onCoordinateScale(o, mode) {
  const text = j_coord_selected.val();

  if (typeof text !== 'undefined' && text !== '') {
    const match = text.match(/(-?[0-9]+)\s(-?[0-9]+)/i);
    let x = match[1], z = match[2];

    if (typeof x !== 'undefined' && typeof z !== 'undefined') {
      x = parseInt(x);
      z = parseInt(z);

      if (mode === 0) {
        x /= 8;
        z /= 8;
      } else {
        x *= 8;
        z *= 8;
      }

      x = Math.floor(x);
      z = Math.floor(z);

      j_coord_selected.val(`${x} ${z}`);
      console.log('Coordinate scaled');
    }
  }
}

function onCoordinateReset(o) {
  const orig = j_coord_selected.attr('original');
  if (typeof orig !== 'undefined') {
    j_coord_selected.val(orig);
    console.log('Coordinate reset');
  }
}

function onCoordinateCopy(o) {
  const text = j_coord_selected.val();

  if (typeof text !== 'undefined' && text !== '') {
    copyToClipboard(text);
    console.log('Coordinate copied', text);
  }
}

function copyToClipboard(text) {
  if (window.clipboardData && window.clipboardData.setData) {
    // IE specific code path to prevent textarea being shown while dialog is visible.
    return window.clipboardData.setData("Text", text);
  } else if (document.queryCommandSupported && document.queryCommandSupported("copy")) {
    var textarea = document.createElement("textarea");
    textarea.textContent = text;
    textarea.style.position = "fixed";  // Prevent scrolling to bottom of page in MS Edge.
    document.body.appendChild(textarea);
    textarea.select();
    try {
      return document.execCommand("copy");  // Security exception may be thrown by some browsers.
    } catch (ex) {
      console.warn("Copy to clipboard failed.", ex);
      return false;
    } finally {
      document.body.removeChild(textarea);
    }
  }
}

let stomp;

function graphData(tracks) {
  markersByDimension.forEach(mark => {
    mark._track_id = [];
    mark.x = [];
    mark.y = [];
  })

  tracks.forEach(track => {
    const nether = track.dimension === 'NETHER';
    const dim = !nether
        ? markersOverworld
        : markersNether;

    if(dim === undefined) {
      console.error(`unknown dimension ${track.dimension}`);
      return;
    }

    dim._track_id.push(track.trackId);
    dim.x.push(nether ? track.x * 8 : track.x);
    dim.y.push(nether ? track.z * 8 : track.z);
    // dim.markers.opacity[i] = 1;

    const mark = markersByHistory.find(m => m._current_track_id === track.trackId);
    if(mark !== undefined) {
      mark.x.unshift(nether ? track.x * 8 : track.x);
      mark.y.unshift(nether ? track.z * 8 : track.z);
    }
  });

  Plotly.redraw(j_map[0]);
  window.setTimeout(updateData, 1000);
}

function connectSocket() {
  const socket = new SockJS('/websocket');
  stomp = Stomp.over(socket);
  stomp.connect({}, function (o) {
    console.log("Connected to websocket");

    // const url = stomp.ws._transport.url;
    // stompSession = /ws:\/\/.*?\/websocket\/[0-9]+?\/([A-Za-z0-9]+)\/websocket/.exec(url)[1];
    //
    // console.log(`session id is ${stompSession}`);

    stomp.subscribe(`/ws-user/ws-subscribe/tracker`, function (response) {
      graphData(JSON.parse(response.body));
    });

    updateData();
  });
}

function updateData() {
  stomp.send('/ws-api/tracking', {}, JSON.stringify({
    server: j_server.val(),
    duration: 10000
  }));
}

$(function () {
  $('[data-toggle="tooltip"]').tooltip({
    trigger: 'hover'
  });

  $('[rel="tooltip"]').on('click', function () {
    $(this).tooltip('hide')
  });

  settings.forEach(e => {
    const id = e.attr('id');
    const value = window.localStorage.getItem(id);
    if (value !== undefined) {
      e.val(value);
    }
    e.change(() => window.localStorage.setItem(id, e.val()))
  });

  graphInitialize();
  graphResize();

  connectSocket();

  window.onclose = function () {
    stomp.disconnect();
  };
});

$(window).resize(graphResize);
