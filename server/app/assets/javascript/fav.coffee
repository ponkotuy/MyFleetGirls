$(document).ready ->
  loadFavCounter(location.pathname + location.search + location.hash)

@loadFavCounter = (path) ->
  $('.btn-add-favorite').click ->
    $.ajax(
      type: 'put'
      url: '/passwd/put/v1/fav'
      data: {url: path}
    ).done () -> favCounter(path)
    .fail (e) ->
      if e.status == 401
        location.href = '/passwd/entire/login?back=' + encodeURIComponent(path)
      else
        console.error(e)

  favCounter(path)

favCounter = (path) ->
  $.get('/rest/v1/fav_count/' + encodeURIComponent(path))
    .done (data) ->
      $('.fav-counter').val(data)
  $.get('/rest/v1/is_faved/' + encodeURIComponent(path))
    .done (data) ->
      if data == 'true'
        $('.btn-add-favorite').attr('disabled', 'disabled')
