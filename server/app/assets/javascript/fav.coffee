$(document).ready ->
  loadFavCounter()

@loadFavCounter = () ->
  $('.favorite-group').each (i, elem) ->
    path = $(elem).attr('data-path')
    path ?= location.pathname + location.search + location.hash
    title = $(elem).attr('data-title')
    title ?= document.title
    btn = $(elem).find('.btn-add-favorite')
    counter = $(elem).find('.fav-counter')
    btn.click -> addFavorite(btn, counter, path, title)
    btn.each -> checkButton(btn, path)
    counter.each -> favCounter(counter, path)

addFavorite = (btn, counter, path, title) ->
  $.ajax(
    type: 'put'
    url: '/passwd/put/v1/fav'
    data: {url: path, title: title}
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
