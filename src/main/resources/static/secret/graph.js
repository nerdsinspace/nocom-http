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
  Plotly.newPlot(j_map[0], markers, plotLayout, plotConfig);

  j_map[0].on('plotly_click', function(data) {
    const point = data.points[0];
    j_coord_selected.val(`${point.x} ${point.y}`);
    j_coord_selected.attr('original', j_coord_selected.val());

    setSelectedMarker(point.x, point.y);
  });
  //onUpdate();
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

function toTimeString(ms) {
  return new Date(ms).toLocaleString();
}

function plotLocations() {
  j_coord_selected.val('');

  // clear array
  markers.length = 0;

  // remake the plot
  graphInitialize();

  const options = {
    groupingRange: parseInt(j_range.val(), 10),
    minDelta: parseInt(j_delta.val(), 10),
    minHits: parseInt(j_hits.val(), 10)
  };

  if (j_server.val() !== '') {
    options.server = j_server.val();
  }

  if (j_dimension.val() !== 'any') {
    options.dimension = parseInt(j_dimension.val(), 10);
  }

  if (j_start_date.val() !== '') {
    options.startTime = new Date(j_start_date.val()).getTime();
  }

  if (j_end_date.val() !== '') {
    options.endTime = new Date(j_end_date.val()).getTime();
  }

  getLocations(options).then(data => {
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
  });
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
  $('#settings-collapse').collapse('hide');
  plotLocations();
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
});

$(window).resize(graphResize);
