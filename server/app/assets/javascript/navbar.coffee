$(document).ready ->
  now = (localStorage.getItem('sound') != 'false')
  change(now)

  href = $('#loginA').attr('href') + '#' + encodeURI(location.pathname)
  $('#loginA').attr('href', href)

  $('#toggle_sound').click () ->
    now = !now
    change(now)

change = (flag) ->
  localStorage.setItem('sound', flag)
  $('#toggle_sound').removeClass('glyphicon-volume-up')
  $('#toglle_sound').removeClass('glyphicon-volume-off')
  if flag
    $('#toggle_sound').addClass('glyphicon-volume-off')
  else
    $('#toggle_sound').addClass('glyphicon-volume-up')
