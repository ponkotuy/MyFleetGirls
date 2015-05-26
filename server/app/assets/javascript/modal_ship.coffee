
# Require graph.coffee

fixWidth = ->
  width = $('div.modal-body').width()
  $('.width-adj').width(width)

$(document).ready ->
  userid = $('#userid').val()
  shipid = $('#shipid').val()
  fixWidth()
  $.getJSON "/rest/v2/user/#{userid}/ship/#{shipid}/exp", (data) ->
    if data.length > 2
      raw = trans(data)
      graph = new Graph('exp')
      graph.overviewPlot(raw)
      graph.wholePlot(raw)
    else
      noDataGraph($('#exp'))

@tweet = (name, lv) ->
  here = encodeURIComponent(location.href)
  text = "#{name} Lv#{lv}"
  url = "https://twitter.com/intent/tweet?original_referer=#{here}&url=#{here}&text=#{text}&hashtags=MyFleetGirls";
  window.open(url)

# data required {userId: `userId`, shipId: `shipId`}
@yome = (userId, shipId) ->
  $.post('/passwd/post/v1/settings', {userId: userId, shipId: shipId})
    .success -> location.href = "/user/#{userId}/top"
    .error (e) -> console.error(e)

# set exp chart
trans = (data) -> [
  data: data.map (x) -> [x['created'], x['exp']]
  label: '経験値'
]

noDataGraph = (node) ->
  node.replaceWith('<p>グラフ生成に必要なデータが充分にありません</p>')
