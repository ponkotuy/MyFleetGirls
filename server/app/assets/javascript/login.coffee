$(document).ready ->
  initUser = $('#initUser').val()
  back = $('#back').val()
  storageKey = 'base_select_number'
  vue = new Vue
    el: '#login_form'
    data:
      userId: initUser
      users: []
      password: ""
      errorMsg: ""
      admiralName: ""
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
      getNumber: () ->
        $('#base_select option:selected')[0].value
      getUsers: (number) ->
        $.getJSON("/rest/v1/search_base_user/#{number}")
        .done (data) -> vue.$set('users', data)
      setAdmiral: (id) ->
        @userId = id
    ready: ->
      number = localStorage.getItem(storageKey) ? @getNumber()
      $('#base_select').val(number)
      @getUsers(number)

  $('#base_select').change ->
    number = vue.getNumber()
    localStorage.setItem(storageKey, number)
    vue.getUsers(number)
