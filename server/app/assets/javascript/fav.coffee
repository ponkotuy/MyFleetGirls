$(document).ready ->
  loadFavCounter()

@loadFavCounter = (path) ->
  $('.favorite-group').each ->
    path = $(this).attr('data-path')
    path = if path then path else (location.pathname + location.search + location.hash)
    btn = $(this).find('.btn-add-favorite')
    counter = $(this).find('.fav-counter')
    btn.click -> addFavorite(btn, counter, path)
    btn.each -> checkButton(btn, path)
    counter.each -> favCounter(counter, path)

addFavorite = (btn, counter, path) ->
  $.ajax(
    type: 'put'
    url: '/passwd/put/v1/fav'
    data: {url: path}
  ).done( ->
    checkButton(btn, path)
    favCounter(counter, path)
  ).fail (e) ->
    if e.status == 401
      location.href = '/passwd/entire/login?back=' + encodeURIComponent(path)
    else
      console.error(e)

favCounter = (counter, path) ->
  $.get('/rest/v1/fav_count/' + encodeURIComponent(path))
    .done (data) ->
      counter.val(data)

checkButton = (btn, path) ->
  $.get('/rest/v1/is_faved/' + encodeURIComponent(path))
    .done (data) ->
      if data == 'true'
        btn.attr('disabled', 'disabled')
