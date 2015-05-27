
# Required Flot, MomentLocales, Lodash

nowGroup = 1
groupButtons = []
plot = null

@setGroup = (n) ->
  $('button.group-button').removeClass('disabled')
  nowGroup = n
  drawGraph()

setGroupButtonClick = ->
  $('button.group-button').each ->
    group = parseInt($(this).attr('data-group'))
    $(this).click ->
      setGroup(group)
      $(this).addClass('disabled')

fixWidth = ->
  width = $('div.tab-content').width()
  $('.width-adj').width(width)

trans = (data) ->
  data.map (ship) ->
    label: ship.name
    data: ship.exps.map (x) -> [x['created'], x['exp']]

graphOption =
  xaxis: { mode: 'time', timezone: 'browser' }
  yaxes: [{}, { alignTicksWithAxis: 1, position: 'right' }]
  selection: { mode: 'x' }
  legend: { position: 'nw' }
  colors: ['red', 'green', 'blue', 'purple', 'gray']

drawGraph = ->
  userid = $('#userid').val()
  $.getJSON "/rest/v2/user/#{userid}/ship/exps/#{nowGroup}", (data) ->
    if data.length > 0
      raw = trans(data)
      plot = $.plot('#ship_exps_graph', raw, graphOption)

$(document).ready ->
  setGroupButtonClick()
  fixWidth()
  drawGraph()
