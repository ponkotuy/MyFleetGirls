google.load('visualization', '1', {packages: ['corechart']})

$(document).ready ->
  userid = $('#userid').val()
  google.setOnLoadCallback ->
  $.get "/rest/v1/#{userid}/materials", (data) ->
    options =
      title: 'Material Transitional'
      hAxis: {title: 'Datetime(sec)'}
      lineWidth: 1

    table1 = google.visualization.arrayToDataTable(translate1(data))
    elem1 = document.getElementById('material_chart')
    chart1 = new google.visualization.ScatterChart(elem1)
    chart1.draw(table1, options)

    table2 = google.visualization.arrayToDataTable(translate2(data))
    elem2 = document.getElementById('otherm_chart')
    chart2 = new google.visualization.ScatterChart(elem2)
    chart2.draw(table2, options)

translate1 = (data) ->
  [['Datetime', '燃料', '弾薬', '鉄鋼', 'ボーキサイト']].concat(
    JSON.parse(data).map (x) ->
      [x['created']/1000, x['fuel'], x['ammo'], x['steel'], x['bauxite']]
  )

translate2 = (data) ->
  [['Datetime', '高速建造材', '高速修理材', '開発資材']].concat(
    JSON.parse(data).map (x) ->
      [x['created']/1000, x['instant'], x['bucket'], x['develop']]
  )
