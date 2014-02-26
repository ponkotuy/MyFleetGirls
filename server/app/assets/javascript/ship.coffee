$(document).ready ->
  $.extend $.tablesorter.themes.bootstrap,
    header     : 'bootstrap-header'
    sortNone   : 'bootstrap-icon-unsorted'
    sortAsc    : 'icon-chevron-up glyphicon glyphicon-chevron-up'
    sortDesc   : 'icon-chevron-down glyphicon glyphicon-chevron-down'

  $('#ship_table').tablesorter
    sortList: [[2, 1]]
    theme: 'bootstrap'
    headerTemplate : '{content} {icon}'
    widgets : ["uitheme"]
