
$(document).ready ->
  moment.lang('ja')

  changeTime = ->
    $('.viewTime').each () ->
      time = parseInt($(this).attr('data-time'))
      diff = moment(time).diff(moment(), 'minutes')
      text = if diff > 0 then "あと#{diff}分" else '修復完了'
      $(this).text(text)

  changeTime()
  timer = setInterval(changeTime, 5000)
