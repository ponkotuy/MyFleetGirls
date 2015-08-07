
# Require lodash & MomentLocale

option =
  xaxis: { mode: 'time', timezone: 'browser' }
  yaxes: [{}, { alignTicksWithAxis: 1, position: 'right' }]
  selection: { mode: 'x' }
  legend: { position: 'nw' }
  colors: ['red', 'green', 'blue', 'purple']
optionO =
  series: { lines: { show: true, lineWidth: 1 }, shadowSize: 0 }
  xaxis: { mode: 'time', timezone: 'browser' }
  yaxes: [{}, { alignTicksWithAxis: 1, position: 'right' }]
  selection: { mode: 'x' }
  legend: { position: 'nw' }
  colors: ['red', 'green', 'blue', 'purple']

class @Graph
  chart: ''
  overview: ''

  constructor: (@name) ->
    @chart = '#' + @name + '_graph'
    @overview = '#' + @name + '_overview'

  plot: null

  wholePlot: (raw) ->
    @mainPlot raw, 'whole', moment().subtract(1, 'months').valueOf()

  mainPlot: (raw, active, min, max = moment().valueOf()) ->
    min_ = min ?= 0
    rangeData = raw[0].data.filter (x) -> min_ < x[0] and x[0] < max
    rangeExps = rangeData.map (x) -> x[1]
    option.yaxis = {min: _.min(rangeExps)}
    first = _.min(rangeData.map (x) -> x[0])
    if min?
      min = Math.max(min, first)
      newOpt = $.extend true, {}, option,
        xaxis: { min: min, max: max }
      @plot = $.plot(@chart, raw, newOpt)

      # Delete plot if no data
      y = @plot.getYAxes()[0]
      yDiff = y.max - y.min
      if yDiff < 1
        $(@chart).replaceWith('')
    else
      @plot = $.plot(@chart, raw, option)

  overviewPlot: (raw) ->
    $.plot(@overview, raw, optionO)
