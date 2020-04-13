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
const markers = [];

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

function graphInitialize() {
  // clear selection
  setCoordSelected();

  // clear markers array
  markers.length = 0;

  Plotly.newPlot(j_map[0], markers, plotLayout, plotConfig);

  j_map[0].on('plotly_click', function(data) {
    const point = data.points[0];
    setCoordSelected(`${point.x} ${point.y}`);
    setSelectedMarker(point.x, point.y);
  });
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

function graphMarkers(data) {
  if (!Array.isArray(data)) {
    return;
  }

  const categories = [
    {
      min: 1,
      color: 'rgb(70, 130, 180)',
      size: 4,
      points: [],
    },
    {
      min: 2,
      color: 'rgb(0, 128, 0)',
      size: 6,
      points: [],
    },
    {
      min: 4,
      color: 'rgb(255,215,0)',
      size: 7,
      points: [],
    },
    {
      min: 8,
      color: 'rgb(255,165,0)',
      size: 8,
      points: [],
    },
    {
      min: 16,
      color: 'rgb(255,69,0)',
      size: 9,
      points: [],
    },
    {
      min: 32,
      color: 'rgb(255,0,0)',
      size: 10,
      points: [],
    },
  ];

  function findCategory(min) {
    let best = undefined;
    for(const c in categories) {
      const o = categories[c];
      if(o.min === min)
        return o;
      else if(o.min > min)
        break;
      else
        best = o;
    }
    return best;
  }

  data.forEach(loc => {
    const infos = [];
    const count = loc.positions.length;

    infos.push(`First hit: ${toTimeString(loc.positions[0].time)}`);
    if (count > 1) {
      infos.push(`Latest hit: ${toTimeString(loc.positions[count - 1].time)}`)
    }
    infos.push(`Hit(s): ${count}`);

    const pnt = findCategory(count);

    if(pnt !== undefined) {
      pnt.points.push({
        x: loc.x,
        y: loc.z,
        info: infos.join('<br>'),
      });
    }
  });

  categories.forEach(cat => {
    const points = cat.points;
    markers.push({
      x: points.map(p => p.x),
      y: points.map(p => p.y),
      text: points.map(p => p.info),
      mode: 'markers',
      hoverinfo: "text+x+y",
      type: 'scattergl',
      marker: {
        color: cat.color,
        size: cat.size,
      },
    });
  });

  graphUpdate();
  graphRecenter();
}

async function getLocations(options) {
  try {
    return await $.ajax({
      type: 'POST',
      url: 'api/search/group/locations',
      data: JSON.stringify(options),
      contentType: 'application/json',
      dataType: 'json',
    });
  } catch (e) {
    console.error(e);
  }
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
  if(typeof text == 'undefined' || text === '') {
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

  if(typeof m === 'undefined') {
    markers.splice(0, 0, data);
  } else {
    m.x = data.x;
    m.y = data.y;
  }

  graphUpdate();
}

function onSubmit(o) {
  const $this = $(o);
  $this.attr('disabled', true);
  $this.tooltip('hide');

  // hide settings
  $('#settings-collapse').collapse('hide');

  // reinitialize the graph
  graphInitialize();

  const onFinish = (e) => $this.attr('disabled', false);

  try {
    return getLocations(queryOptions())
        .then(graphMarkers)
        .finally(onFinish);
  } catch (e) {
    onFinish(e);
  }
}

function onCoordinateScale(o, mode) {
  const text = j_coord_selected.val();

  if(typeof text !== 'undefined' && text !== '') {
    const match = text.match(/(-?[0-9]+)\s(-?[0-9]+)/i);
    let x = match[1], z = match[2];

    if(typeof x !== 'undefined' && typeof z !== 'undefined') {
      x = parseInt(x);
      z = parseInt(z);

      if(mode === 0) {
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
  if(typeof orig !== 'undefined') {
    j_coord_selected.val(orig);
    console.log('Coordinate reset');
  }
}

function onCoordinateCopy(o) {
  const text = j_coord_selected.val();

  if(typeof text !== 'undefined' && text !== '') {
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
let updateInterval;

function connectSocket() {
  const socket = new SockJS('/websocket');
  stomp = Stomp.over(socket);
  stomp.connect({}, function (frame) {
    console.log("Connected to websocket");

    stomp.subscribe('/nocom/subscribe/tracker', function (response) {
      // console.log('recieved message', tracks);
      const tracks = JSON.parse(response.body);

      const data = {
        overworld: {
          x: [],
          z: [],
          color: 'rgb(0,255,0)'
        },
        nether: {
          x: [],
          z: [],
          color: 'rgb(255,0,0)'
        }
      }

      tracks.forEach(track => {
        const tbl = track.dimension === "OVERWORLD" ? data.overworld : data.nether;
        tbl.x.push(track.overworldBlockX);
        tbl.z.push(track.overworldBlockZ);
      });

      markers.length = 0;
      [data.overworld, data.nether].forEach(e => {
        markers.push({
          x: e.x,
          y: e.z,
          // text: points.map(p => p.info),
          mode: 'markers',
          hoverinfo: "text+x+y",
          type: 'scattergl',
          marker: {
            color: e.color,
            size: 7,
          },
        });
      });

      graphUpdate();
    })

    if(updateInterval === undefined) {
      updateData();
      updateInterval = window.setInterval(updateData, 100);
    }
  });
}

function updateData() {
  stomp.send('/nocom/sock/tracking', {}, JSON.stringify({
    server: j_server.val()
  }));
}

$(function() {
  $('[data-toggle="tooltip"]').tooltip({
    trigger : 'hover'
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
});

$(window).resize(graphResize);
