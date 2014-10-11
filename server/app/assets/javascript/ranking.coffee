$(document).ready ->
  # Initial
  param = fromURLParameter(location.hash.replace(/^\#/, ''))
  if param.detail?
    $('#modal').modal({remote: "rank/#{param.detail}"})

  # Modal Hook
  $('#modal').on 'shown.bs.modal', (e) ->
    detail = $(e.relatedTarget).attr('data-name')
    if detail?
      location.hash = toURLParameter({detail: detail})
  $('#modal').on 'hidden.bs.modal', ->
    url = location.href.split('#')[0]
    history.pushState(null, null, url)
    $(this).removeData('bs.modal')
