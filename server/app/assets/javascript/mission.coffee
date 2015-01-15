$(document).ready ->
  userid = $('#userid').val()
  vue = new Vue
    el: '#mission'
    data:
      stage: 'ALL'
      missions: []
    methods:
      getData: ->
        dat = {missionId: @missionId(), limit: 20, offset: 0}
        $.getJSON "/rest/v2/user/#{userid}/mission", dat, (ret) =>
          @missions = ret
      missionId: ->
        if @stage == 'ALL' then undefined else missionId: parseInt(@stage)
    compiled: ->
      @getData()
    watch:
      'stage': -> @getData()
