
$(document).ready ->
  moment.locale('ja')

  changeTime = ->
    $('.viewTimeBefore').each () ->
      time = parseInt($(this).attr('data-time'))
      end_mes = $(this).attr('data-end-mes')
      diff = moment(time).diff(moment(), 'minutes')
      text = if diff > 0
        "あと#{diff}分"
      else if diff <= 0
        end_mes
      else ""
      $(this).text(text)

  changeTime()
  timer = setInterval(changeTime, 1000)

  $('.viewTime').each () ->
    millis = parseInt($(this).attr('data-time'))
    text = moment(millis).format('YYYY-MM-DD HH:mm')
    $(this).text(text)
