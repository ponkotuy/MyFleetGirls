$(document).ready ->
  userid = $('#userid').val()
  setSubmitClick(userid)
  setVisibleClick(userid)

setSubmitClick = (userid) ->
  $('#submit').click ->
    selected = $('input[name="honor"]:checked').val()
    if selected
      $.post('/passwd/post/v1/set_honor', {userId: userid, name: selected})
      .success  ->
        location.reload(true)

setVisibleClick = (userid) ->
  $('span.click-visible').click ->
    name = $(this).attr('data-name')
    patchVisible(userid, name, false)
  $('span.click-invisible').click ->
    name = $(this).attr('data-name')
    patchVisible(userid, name, true)

patchVisible = (userid, name, value) ->
  $.ajax('/post/v1/honor/invisible', {method: 'PATCH', data: {userId: userid, name: name, value: value}})
    .success -> location.reload()
