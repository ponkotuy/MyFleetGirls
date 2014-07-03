$(document).ready ->
  initUser = $('#initUser').val()
  back = $('#back').val()
  vue = new Vue
    el: '#login_form'
    data:
      userId: initUser
      password: ""
      errorMsg: ""
    methods:
      submit: () ->
        data = {userId: @userId, password: @password}
        href = if back then back else '/'
        $.post('/passwd/post/v1/set_session', data)
          .done( => location.href = href)
          .fail((str) => @errorMsg = str)
