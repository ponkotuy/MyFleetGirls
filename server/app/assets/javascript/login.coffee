$(document).ready ->
  vue = new Vue
    el: '#login_form'
    data:
      userId: ""
      password: ""
      errorMsg: ""
    methods:
      submit: () ->
        data = {userId: @userId, password: @password}
        href = location.hash[1..] ? '/'
        $.post('/passwd/post/v1/set_session', data)
          .done( => location.href = href)
          .fail((str) => @errorMsg = str)
