$(document).ready ->
  userId = parseInt($('#userid').val())
  deckport = parseInt($('#deckport').val())
  vue = new Vue
    el: '#snapshot'
    data:
      title: ""
      comment: ""
      errorMsg: {}
    methods:
      submit: () ->
        data = {title: @title, comment: @comment, userId: userId, deckport: deckport}
        $.post('/passwd/post/v1/register_snap', data)
          .done( -> location.href = "/user/#{userId}/snapshot")
          .fail((str) => @errorMsg = str)
