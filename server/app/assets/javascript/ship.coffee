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
    widgets: ["uitheme"]

  $('#modal').on 'shown.bs.modal', ->
    $('.ship_hbar').each () ->
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
    $(this).removeData('bs.modal')
