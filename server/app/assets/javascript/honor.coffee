$(document).ready ->
  userid = $('#userid').val()
  console.log($('#submit'))
  $('#submit').click ->
    selected = $('input[name="honor"]:checked').val()
    if selected
      $.post('/passwd/post/v1/set_honor', {userId: userid, name: selected})
        .success  ->
          location.reload(true)
