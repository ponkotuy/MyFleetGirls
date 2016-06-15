$(document).ready ->
  userid = $('#userid').val()
  vue = new Vue
    el: "#quest"
    data:
      daily_quests: []
      weekly_quests: []
      monthly_quests: []
      once_quests: []
      others_quests: []
    methods:
      progressView: (state, progress, manual) ->
        if manual then return '達成'
        switch state
          when 1 then '未受注'
          when 3 then '達成'
          else
            switch progress
              when 1 then '50%以上達成'
              when 2 then '80%以上達成'
              else '受注'
      catView: (cat) ->
        [undefined, '編成', '出撃', '演習', '遠征', '補給/入渠', '工廠', '改装'][cat]
      getJson: () ->
        $.getJSON "/rest/v1/#{userid}/quest", {}, (data) =>
          @daily_quests = data.filter (x) -> x.typ == 1
          @weekly_quests = data.filter (x) -> x.typ == 2
          @monthly_quests = data.filter (x) -> x.typ == 3
          @once_quests = data.filter (x) -> x.typ == 4
          @others.quests = data.filter (x) -> x.typ == 5
      manualFlag: (id) ->
        $.ajax('/post/v1/quest/manual_flag', {type: 'PATCH', data: {userId: userid, id: id}})
          .success -> location.reload()
      setTooltip: () ->
        $('tr.tltip').tooltip({html: true})
    created: () ->
      @getJson()
    ready: () ->
      @$watch 'once_quests', @setTooltip
