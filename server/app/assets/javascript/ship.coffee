$(document).ready ->
  $.extend $.tablesorter.themes.bootstrap,
    header: 'bootstrap-header'
    sortNone: 'bootstrap-icon-unsorted'
    sortAsc: 'icon-chevron-up glyphicon glyphicon-chevron-up'
    sortDesc: 'icon-chevron-down glyphicon glyphicon-chevron-down'

  $('#ship_table').tablesorter
    sortList: [[3, 1], [4, 1]]
    theme: 'bootstrap'
    headerTemplate: '{content} {icon}'
    widgets: ['uitheme']

  param = fromURLParameter(location.hash.replace(/^\#/, ''))
  if param.modal?
    $('#modal').modal({remote: "aship/#{param.id}"})

  $('#modal').on 'shown.bs.modal', (e) ->
    $('.ship_hbar').each () ->
      id = JSON.parse($(this).attr('data-id'))
      location.hash = toURLParameter({modal: true, id: id})
      data = JSON.parse($(this).attr('data-json'))
      option =
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
      $(this).jqplot(data, option)

  $('#modal').on 'hidden.bs.modal', ->
    location.hash = ''
    $(this).removeData('bs.modal')
