$(document).ready ->
  userId = parseInt($('#userid').val())
  vue = new Vue
    el: '#snapshot'
    data:
      comment: ""
      password: ""
      errorMsg: {}
    methods:
      submit: () ->
        data = {comment: comment, userId: userId, password: @password}
        $.post('/passwd/v1/register', data)
          .done( => location.href = "/user/#{@userId}")
          .fail((str) => @errorMsg = str)
