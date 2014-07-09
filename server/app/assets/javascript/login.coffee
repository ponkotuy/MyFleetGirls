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
        if isNaN(parseInt(@userId))
          console.error('UserID is non number.')
          @errorMsg = {status: 499, statusText: 'User side error', responseText: 'UserIDが数字ではありません'}
        else
          data = {userId: @userId, password: @password}
          href = if back then back else '/'
          $.post('/passwd/post/v1/set_session', data)
            .done( => location.href = href)
            .fail((err) => @errorMsg = err)
