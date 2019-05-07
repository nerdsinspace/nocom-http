
const ViewGraph = (function() {
  const jServer = $('#server');
  const jDimension = $('#dimension');
  const jRange = $('#range');
  const jDelta = $('#delta');
  const jHits = $('#hits');
  const jStartDate = $('#startDate');
  const jEndDate = $('#endDate');
  const jSubmit = $('#submit');
  const jSelectedPos = $('#selectedPos');

  const settings = [jServer, jDimension, jRange, jDelta, jHits, jStartDate, jEndDate];

  const jMap = $('#map');
  const map = jMap[0];
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

  const setupLayout = () => {
    const m = document.getElementById('map');

    Plotly.react(m, markers, plotLayout, plotConfig);

    m.on('plotly_click', function(data) {
      const point = data.points[0];
      jSelectedPos.val(point.x + ' ' + point.y);
    });
  };

  const initialize = () => {
    plotLayout.width = (map.width);
    plotLayout.height = (map.height);

    setupLayout();
    recenter();
  };

  const recenter = () => {
    Plotly.relayout('map', {
      'xaxis.autorange': true,
      'yaxis.autorange': true
    });
  };

  const getLocations = async (options) => {
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
  };

  const toTimeString = (ms) => {
    return new Date(ms).toLocaleString();
  };

  const loadMarkers = () => {
    const options = {
      groupingRange: parseInt(jRange.val(), 10),
      minDelta: parseInt(jDelta.val(), 10),
      minHits: parseInt(jHits.val(), 10)
    };

    if(jServer.val() !== '')
      options.server = jServer.val();

    if(jDimension.val() !== 'any')
      options.dimension = parseInt(jDimension.val(), 10);

    if(jStartDate.val() !== '')
      options.startTime = new Date(jStartDate.val()).getTime();

    if(jEndDate.val() !== '')
      options.endTime = new Date(jEndDate.val()).getTime();

    getLocations(options).then(data => {
      Plotly.purge(map);

      if(!Array.isArray(data))
        return;

      // clear array
      markers.length = 0;

      const points = [];

      data.forEach(loc => {
        const infos = [];
        const count = loc.positions.length;

        infos.push(`First hit: ${toTimeString(loc.positions[0].time)}`);
        if(count > 1) {
          infos.push(`Latest hit: ${toTimeString(loc.positions[count - 1].time)}`)
        }
        infos.push(`Hit(s): ${count}`);

        points.push({
          x: loc.x,
          y: loc.z,
          info: infos.join('<br>'),
        });
      });

      markers.push({
        x: points.map(p => p.x),
        y: points.map(p => p.y),
        text: points.map(p => p.info),
        mode: 'markers',
        hoverinfo: "text+x+y",
        type: 'scattergl',
      });

      setupLayout();
      recenter();
    });
  };

  settings.forEach(e => {
    const id = e.attr('id');
    const saved = window.localStorage.getItem(id);
    if(saved !== undefined) e.val(saved);
    e.change(() => window.localStorage.setItem(id, e.val()))
  });

  // submit button event
  jSubmit.click(() => {
    Plotly.purge(map);
    loadMarkers();
  });

  window.addEventListener('resize', initialize);

  return {initialize};
})();

$(document).ready(function() {
  ViewGraph.initialize();
});