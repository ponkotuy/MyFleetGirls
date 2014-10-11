chart = '#admiral_exp'
overview = '#admiral_exp_overview'

option =
  xaxis: { mode: 'time', timezone: 'browser' }
  yaxes: [{}, { alignTicksWithAxis: 1, position: 'right' }]
  selection: { mode: 'x' }
  legend: { position: 'nw' }
optionO =
  series: { lines: { show: true, lineWidth: 1 }, shadowSize: 0 }
  xaxis: { mode: 'time', timezone: 'browser' }
  yaxes: [{}, { alignTicksWithAxis: 1, position: 'right' }]
  selection: { mode: 'x' }
  legend: { position: 'nw' }

plot = null
exps = []

$(document).ready ->
  userid = $('#userid').val()
  $.getJSON "/rest/v1/#{userid}/basics", (data) ->
    exps = translate(data)
    plotO = $.plot(overview, exps, optionO)
    wholePlot()

translate = (data) -> [
  data: data.map (x) -> [x['created'], x['experience']]
  label: '提督経験値'
]

wholePlot = -> mainPlot 'whole', moment().subtract(1, 'months').valueOf()

mainPlot = (active, min, max = moment().valueOf()) ->
  min_ = min ?= 0
  console.log(min_)
  console.log(exps[0].data)
  rangeData = exps[0].data.filter (x) -> min_ < x[0] and x[0] < max
  rangeExps = rangeData.map (x) -> x[1]
  option.yaxis = {min: _.min(rangeExps)}
  console.log(option.yaxis)
  if min?
    newOpt = $.extend true, {}, option,
      xaxis: { min: min, max: max }
    plot = $.plot(chart, exps, newOpt)
  else
    plot = $.plot(chart, exps, option)
