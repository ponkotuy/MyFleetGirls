
$(document).ready ->
  setDeleteButtonEvent()
  setModalEvent()
  url = urlParam(location.hash.replace(/^\#/, ''))
  if url?
    $('#modal').modal({remote: 'fav_froms?url=' + encodeURIComponent(url)})

setDeleteButtonEvent = ->
  $('.fav-delete').each ->
    id = parseInt($(this).attr('data-id'))
    $(this).click ->
      $.ajax(
        type: 'delete'
        url: '/passwd/delete/v1/fav/' + id
      ).done ->
        location.reload()

setModalEvent = ->
  $('#modal').on 'shown.bs.modal', (e) ->
    url = $(e.relatedTarget).attr('data-url')
    if url?
      location.hash = toURLParameter({url: encodeURIComponent(url)})
  $('#modal').on 'hidden.bs.modal', (e) ->
    url = location.href.split('#')[0]
    history.pushState(null, null, url)
    $(this).removeData('bs.modal')

urlParam = (hash) ->
  ary = hash.split('=')
  key = ary.shift()
  if key == 'url' then ary.join('=') else null
