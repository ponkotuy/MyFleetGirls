vue = null

$(document).ready ->
  vue = new Vue(vueOpt)

@modal_edit = (userId, snapId) ->
  vue.init(userId, snapId)
  $('#mini-modal').modal()

vueOpt =
  el: '#mini-modal'
  data:
    title: ""
    comment: ""
    errorMsg: {}
    userId: 0
    snapId: 0
  methods:
    submit: () ->
      data = {title: $('#title').val(), comment: $('#comment').val(), userId: @userId, snapId: @snapId}
      $.post('/passwd/post/v1/update_snap', data)
        .done( => location.href = "/user/#{@userId}/snapshot")
        .fail((err) => @errorMsg = err)
    init: (userId, snapId) ->
      @userId = userId
      @snapId = snapId
      $.get("/rest/v1/#{userId}/snap/#{snapId}")
        .done (data) =>
          @title = data.title
          @comment = data.comment

