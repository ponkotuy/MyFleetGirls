$(document).ready ->
  $.extend $.tablesorter.themes.bootstrap,
    header: 'bootstrap-header'
    sortNone: 'bootstrap-icon-unsorted'
    sortAsc: 'icon-chevron-up glyphicon glyphicon-chevron-up'
    sortDesc: 'icon-chevron-down glyphicon glyphicon-chevron-down'

  param = fromURLParameter(location.hash.replace(/^\#/, ''))
  param.lv ?= ""
  if param.modal?
    if param.fleet?
      $('#modal').modal({remote: "fleet/#{param.id}"})
    else
      $('#modal').modal({remote: "aship/#{param.id}"})

  $('#ship_table').tablesorter
    sortList: [[3, 1], [4, 1]]
    theme: 'bootstrap'
    headerTemplate: '{content} {icon}'
    widgets: ['uitheme', 'filter']
    widgetOptions:
      filter_hideFilters: true
  $.tablesorter.setFilters($('#ship_table'), ['', '', '', param.lv], true)

  $('#modal').on 'shown.bs.modal', (e) ->
    $('.ship_hbar').each () ->
      id = JSON.parse($(this).attr('data-id'))
      location.hash = toURLParameter({modal: true, id: id})
      data = JSON.parse($(this).attr('data-json'))
      $(this).jqplot(data, jqplotOpt)
    $('.fleet_data').each () ->
      id = parseInt($(this).attr('data-id'))
      location.hash = toURLParameter({modal: true, id: id, fleet: true})

  $('#modal').on 'hidden.bs.modal', ->
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
