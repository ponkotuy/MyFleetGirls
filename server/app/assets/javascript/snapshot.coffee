$(document).ready ->
  param = fromURLParameter(location.hash.replace(/^\#/, ''))
  if param.modal?
    $('#modal').modal({remote: "snap_aship/#{param.id}"})

  $('#modal').on 'shown.bs.modal', (e) ->
    $('.ship_hbar').each ->
      id = JSON.parse($(this).attr('data-id'))
      location.hash = toURLParameter({modal: true, id: id})
      data = JSON.parse($(this).attr('data-json'))
      $(this).jqplot(data, jqplotOpt)
    loadFavCounter()

  $('#modal').on 'hidden.bs.modal',(e)  ->
    url = location.href.split('#')[0]
    history.pushState(null, null, url)
    $(this).removeData('bs.modal')

jqplotOpt =
  seriesDefaults:
    renderer: $.jqplot.BarRenderer
    rendererOptions:
      barDirection: 'horizontal'
      varyBarColor: true
      barMargin: 3
    shadow: false
    pointLabels:
      show: true
  axes:
    yaxis:
      renderer: $.jqplot.CategoryAxisRenderer
    xaxis:
      min: 0
      max: 120
      showTicks: false
      showTickMarks: false
