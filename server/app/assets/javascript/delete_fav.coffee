
$(document).ready ->
  $('.fav-delete').each ->
    id = parseInt($(this).attr('data-id'))
    $(this).click ->
      $.ajax(
        type: 'delete'
        url: '/passwd/delete/v1/fav/' + id
      ).done ->
        location.reload()
