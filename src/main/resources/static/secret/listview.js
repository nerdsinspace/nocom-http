
const ListView = (function() {
  const jServer = $('#server');
  const jDimension = $('#dimension');
  const jRange = $('#range');
  const jDelta = $('#delta');
  const jHits = $('#hits');
  const jStartDate = $('#startDate');
  const jEndDate = $('#endDate');
  const jSubmit = $('#submit');
  const jList = $('#list tbody');


  const initialize = () => {

  };

  const makeCollapsible = (button, div) => {
    button.click(() => {
      if (div[0].style.maxHeight){
        div[0].style.maxHeight = null;
      } else {
        div[0].style.maxHeight = div[0].scrollHeight + "px";
      }
    });

  }


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

  const loadList = () => {
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

    jList.empty();

    getLocations(options).then(data => {
      if(!Array.isArray(data))
        return;


      for (var i = 0; i < data.length; i++) {
        const loc = data[i];
        const info = loc.positions.length <= 1
                  ? new Date(loc.positions[0].time).toLocaleString()
                  : (new Date(loc.positions[0].time).toLocaleString()
                      + " - "
                      + new Date(loc.positions[loc.positions.length - 1].time).toLocaleString());


        const dist = Math.round(Math.sqrt((loc.x * loc.x) + (loc.z * loc.z))) + 'm';
        const biomes = sortedByRelevance(loc.positions.map(p => p.biome));

        var biomeDiv = $('<div class="dropdown-content">');
        biomes.slice(1, biomes.length).forEach(bName => {
          biomeDiv.append($('<p>').text(bName));
        });

        const biomeCell =$('<td class="dropdown">')
          .append(
            $('<button class="dropbtn">').text(biomes[0] != null ? biomes[0] : '')
              .append(biomeDiv)
          );

        var buttonDownloaded;
        var divDownloaded;
        var buttonGenerated;
        var divGenerated;

        const mAligned = 'style="vertical-align: middle"';
        jList.append(
         $('<tr>')
           .append($('<td ' + mAligned + '>').text(loc.x)) // X
           .append($('<td ' + mAligned + '>').text(loc.z)) // Z
           .append($('<td ' + mAligned + '>').text(dist)) // Distance
           .append(biomeCell)

           // TODO: embed iframe
           // TODO: check if renders exist
           // Render
           .append($('<td>')
             .append((buttonDownloaded = $('<button type="button" class = "collapsible">')).text('Downloaded'))
             .append((buttonGenerated = $('<button type="button" class = "collapsible" style="margin-right:0px;">')).text('Generated'))
           )
        );

        // TODO: fix formatting
        jList.append(
         $('<tr align="center">')
         .append(
           $('<td colspan="666">').append(
             (divDownloaded = $('<div class="content">')).text(':^)')
           )
           .append($('<td colspan="666">').append(
              (divGenerated = $('<div class="content">')).text(':^)')
             )
           )
         )
        );


        makeCollapsible(buttonDownloaded, divDownloaded);
        makeCollapsible(buttonGenerated, divGenerated);
      }

    });
  };

  const sortedByRelevance = (biomeList) => {
      var map = {};
      biomeList.forEach(biome => {
        const num = map[biome];
        map[biome] = num != null ? num + 1 : 1;
      });

      return Object.entries(map)
        .sort(entry => entry[1])
        .map(entry => entry[0]);
  }

  // setup server setting
  if(window.localStorage.getItem('server'))
    jServer.val(window.localStorage.getItem('server'));

  jServer.change(() => window.localStorage.setItem('server', jServer.val()));

  // dimension setting
  if(window.localStorage.getItem('dimension'))
    jDimension.val(window.localStorage.getItem('dimension'));

  jDimension.change(() => window.localStorage.setItem('dimension', jDimension.val()));

  // min delta setting
  if(window.localStorage.getItem('delta'))
    jDelta.val(parseInt(window.localStorage.getItem('delta'), 10));

  jDelta.change(() => window.localStorage.setItem('delta', jDelta.val()));

  // min hits setting
  if(window.localStorage.getItem('hits'))
    jHits.val(parseInt(window.localStorage.getItem('hits'), 10));

  jHits.change(() => window.localStorage.setItem('hits', jHits.val()));

  // range setting
  if(window.localStorage.getItem('range'))
    jRange.val(parseInt(window.localStorage.getItem('range'), 10));

  jRange.change(() => window.localStorage.setItem('range', jRange.val()));

  // start date setting
  if(window.localStorage.getItem('startDate'))
    jStartDate.val(window.localStorage.getItem('startDate'));

  jStartDate.change(() => window.localStorage.setItem('startDate', jStartDate.val()));

  // end date setting
  if(window.localStorage.getItem('endDate'))
    jEndDate.val(window.localStorage.getItem('endDate'));

  jEndDate.change(() => window.localStorage.setItem('endDate', jEndDate.val()));

  // submit button event
  jSubmit.click(() => {
    loadList();
  });


  return {initialize};
})();

$(document).ready(function() {
  ListView.initialize();
});