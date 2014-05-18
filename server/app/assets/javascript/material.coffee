chart = '#material_chart'
overview = '#material_overview'

option =
  xaxis: { mode: 'time', timezone: 'browser' }
  yaxes: [{}, { alignTicksWithAxis: 1, position: 'right' }]
  selection: { mode: 'x' }
optionO =
  series: { lines: { show: true, lineWidth: 1 }, shadowSize: 0 }
  xaxis: { mode: 'time', timezone: 'browser' }
  yaxes: [{}, { alignTicksWithAxis: 1, position: 'right' }]
  selection: { mode: 'x' }

plot = null
table = []

$(document).ready ->
  userid = $('#userid').val()
  button()
  $.getJSON "/rest/v1/#{userid}/materials", (data) ->
    table = translate(data)

    param = fromURLParameter(location.hash.replace(/^\#/, ''))
    if param.max?
      mainPlot(param.active, param.min, param.max)
    else
      monthPlot()
    $(chart).bind 'plotselected', (event, ranges) ->
      mainPlot('', ranges.xaxis.from, ranges.xaxis.to)

    plotO = $.plot(overview, table, optionO)
    $(overview).bind('plotselected', (event, ranges) -> plot.setSelection(ranges))

translate = (data) ->
  fuel = { data: transElem(data, 'fuel'), label: '燃料', yaxis: 1 }
  ammo = { data: transElem(data, 'ammo'), label: '弾薬' , yaxis: 1 }
  steel = { data: transElem(data, 'steel'), label: '鉄鋼', yaxis: 1 }
  bauxite = { data: transElem(data, 'bauxite'), label: 'ボーキサイト', yaxis: 1 }
  instant = { data: transElem(data, 'instant'), label: '高速建造材', yaxis: 2 }
  bucket = { data: transElem(data, 'bucket'), label: '高速修復材', yaxis: 2 }
  develop = { data: transElem(data, 'develop'), label: '開発資材', yaxis: 2 }
  [fuel, ammo, steel, bauxite, instant, bucket, develop]

transElem = (data, elem) -> data.map (x) -> [x['created'], x[elem]]

monthPlot = -> mainPlot 'month', moment().subtract('months', 1).valueOf()
weekPlot = -> mainPlot 'week', moment().subtract('weeks', 1).valueOf()
dayPlot = -> mainPlot 'day', moment().subtract('days', 1).valueOf()

mainPlot = (active, min, max = moment().valueOf()) ->
  activeButton(active)
  location.hash = toURLParameter({min: min, max: max, active: active})
  if min?
    newOpt = $.extend true, {}, option,
      xaxis: { min: min, max: max }
    plot = $.plot(chart, table, newOpt)
  else
    plot = $.plot(chart, table, option)

activeButton = (active) ->
  $('#range').children('button.active').map () -> $(this).removeClass('active')
  $('#' + active).addClass('active')

button = ->
  $('#whole').click ->
    mainPlot('whole')
  $('#month').click ->
    monthPlot()
  $('#week').click ->
    weekPlot()
  $('#day').click ->
    dayPlot()
