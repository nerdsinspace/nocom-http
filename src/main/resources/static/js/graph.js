
const ViewGraph = (function() {
  const jServer = $('#server');
  const jDimension = $('#dimension');
  const jRange = $('#range');
  const jStartDate = $('#startDate');
  const jEndDate = $('#endDate');
  const jSubmit = $('#submit');

  const map = $('#map')[0];
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
  };
  const plotConfig = {
    scrollZoom: true,
  };

  const initialize = () => {
    plotLayout.width = (map.width = window.innerWidth);
    plotLayout.height = (map.height = window.innerHeight);

    Plotly.react(map, markers, plotLayout, plotConfig);
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

  const loadMarkers = () => {
    const options = {
      groupingRange: parseInt(jRange.val(), 10),
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

      data.forEach(loc => {
        markers.push({
          x: [loc.x],
          y: [loc.z],
          mode: 'markers',
          name: 'x' + loc.positions.length,
          hoverinfo: "name+x+y"
          //type: 'scatter'
        });
      });

      console.log('added markers');
      Plotly.react(map, markers, plotLayout, plotConfig);
    });
  };

  const loadServers = async () => {
    try {
      return await $.ajax({
        type: 'GET',
        url: 'api/servers',
        dataType: 'json',
      });
    } catch (e) {
      console.error(e);
    }
  };

  const loadDimensions = async () => {
    try {
      return await $.ajax({
        type: 'GET',
        url: 'api/dimensions',
        dataType: 'json',
      });
    } catch (e) {
      console.error(e);
    }
  };

  // setup server setting
  loadServers().then(data => {
    data.forEach(server => {
      jServer.append($('<option>', {value: server, text: server}));
    });

    if(window.localStorage.getItem('server'))
      jServer.val(window.localStorage.getItem('server'));
  });

  jServer.change(() => {
    window.localStorage.setItem('server', jServer.val());
    onInputChanged();
  });

  // dimension setting
  loadDimensions().then(data => {
    data.forEach(dimension => {
      jDimension.append($('<option>', {value: dimension.ordinal, text: dimension.name}));
    });

    if(window.localStorage.getItem('dimension'))
      jDimension.val(window.localStorage.getItem('dimension'));
  });

  if(window.localStorage.getItem('dimension'))
    jDimension.val(window.localStorage.getItem('dimension'));

  jDimension.change(() => {
    window.localStorage.setItem('dimension', jDimension.val());
    onInputChanged();
  });

  // range setting
  if(window.localStorage.getItem('range'))
    jRange.val(parseInt(window.localStorage.getItem('range'), 10));

  jRange.change(() => {
    window.localStorage.setItem('range', jRange.val());
    onInputChanged();
  });

  // start date setting
  if(window.localStorage.getItem('startDate'))
    jStartDate.val(window.localStorage.getItem('startDate'));

  jStartDate.change(() => {
    window.localStorage.setItem('startDate', jStartDate.val());
    onInputChanged();
  });

  // end date setting
  if(window.localStorage.getItem('endDate'))
    jEndDate.val(window.localStorage.getItem('endDate'));

  jEndDate.change(() => {
    window.localStorage.setItem('endDate', jEndDate.val());
    onInputChanged();
  });

  // submit button event
  jSubmit.click(() => {
    Plotly.purge(map);
    loadMarkers();
  });

  const onInputChanged = () => {};

  window.addEventListener('resize', initialize);

  return {initialize};
})();

$(document).ready(function() {
  ViewGraph.initialize();
});