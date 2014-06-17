$(document).ready ->
  $('#modal').on 'hidden.bs.modal', ->
    url = location.href.split('#')[0]
    history.pushState(null, null, url)
    $(this).removeData('bs.modal')
