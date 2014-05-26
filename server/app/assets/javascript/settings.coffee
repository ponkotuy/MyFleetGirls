$(document).ready ->
  vue = new Vue
    el: '#settings'
    data:
      shipId: null
      userId: null
      password: ""
      errorMsg: {}
    methods:
      submit: () ->
        data = {shipId: parseInt(@shipId), userId: parseInt(@userId), password: @password}
        $.post('/post/v1/settings', data)
          .done( => location.href = "/user/#{@userId}")
          .fail((str) => @errorMsg = str)
      fromHash: () ->
        param = fromURLParameter(location.hash.replace(/^\#/, ''))
        @shipId = param.shipId
        @userId = param.userId
        @password = param.password
    created: () ->
      @fromHash()
