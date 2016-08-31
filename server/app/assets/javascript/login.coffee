$(document).ready ->
  initUser = $('#initUser').val()
  back = $('#back').val()
  baseSelectKey = 'base_select_number'
  userIdKey = 'user_id_number'
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
          @errorMsg = {status: 499, statusText: 'User side error', responseText: 'UserIDが数字ではありません'}
        else
          data = {userId: @userId, password: @password}
          href = if back then back else '/'
          $.post('/passwd/post/v1/set_session', data)
            .done( -> location.href = href)
            .fail((err) => @errorMsg = err)
      getNumber: () ->
        $('#base_select option:selected')[0].value
      getUsers: (number, name) ->
        json = if number == "-1"
          $.getJSON("/rest/v1/search_user?q=#{name}")
        else
          $.getJSON("/rest/v1/search_base_user?base=#{number}&q=#{name}")
        json.done (data) -> vue.$set('users', data)
      setAdmiral: (id) ->
        @userId = id
    watch:
      'admiralName': -> @getUsers(@getNumber(), @admiralName)
      'userId': -> localStorage.setItem(userIdKey, @userId)
    ready: ->
      baseSelect = localStorage.getItem(baseSelectKey) ? @getNumber()
      $('#base_select').val(baseSelect)
      @userId = localStorage.getItem(userIdKey) ? ""
      @getUsers(baseSelect, "")

  $('#base_select').change ->
    number = vue.getNumber()
    localStorage.setItem(baseSelectKey, number)
    vue.getUsers(number, vue.admiralName)
