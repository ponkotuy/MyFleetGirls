google.load('visualization', '1', {packages: ['corechart']})

$(document).ready ->
  userid = $('#userid').val()
  google.setOnLoadCallback ->
  $.get "/rest/v1/#{userid}/materials", (data) ->
    table = google.visualization.arrayToDataTable(translate(data))
    options = title: 'Material Transitional'
    elem = document.getElementById('material_chart')
    chart = new google.visualization.ScatterChart(elem)
    chart.draw(table, options)

translate = (data) ->
  [['Datetime', 'Steel']].concat(
    JSON.parse(data).map (x) -> [x['created'], x['steel']]
  )
