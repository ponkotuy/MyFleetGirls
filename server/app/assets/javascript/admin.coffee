$(document).ready ->
  new Vue
    el: '#deletePassword'
    data:
      userId: 0
      userName: ""
      message: ""
    methods:
      getAdmiralName: (uid) ->
        console.log(@userId, uid)
        $.getJSON "/rest/v2/#{uid}", {}, (ret) =>
          @userName = ret.nickname
      deletePass: (uid) ->
        $.ajax
          url: "/rest/v2/#{uid}/password"
          method: 'DELETE'
          success:
            @message = "Success delete password uid = #{uid}"
