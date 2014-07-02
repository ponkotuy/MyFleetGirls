$(document).ready ->
  vue = new Vue
    el: '#settings'
    data:
      shipId: null
      userId: null
      errorMsg: {}
    methods:
      submit: () ->
        data = {shipId: parseInt(@shipId), userId: parseInt(@userId)}
        $.post('/passwd/post/v1/settings', data)
          .done( => location.href = "/user/#{@userId}")
          .fail((str) => @errorMsg = str)
      fromHash: () ->
        param = fromURLParameter(location.hash.replace(/^\#/, ''))
        @shipId = param.shipId
        @userId = param.userId
    created: () ->
      @fromHash()
