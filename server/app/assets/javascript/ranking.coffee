$(document).ready ->
  $('#modal').on 'shown.bs.modal', (e) ->
    detail = $(e.relatedTarget).attr('data-name')
    location.hash = toURLParameter({detail: detail})
  $('#modal').on 'hidden.bs.modal', ->
    url = location.href.split('#')[0]
    history.pushState(null, null, url)
    $(this).removeData('bs.modal')
