
@toURLParameter = (obj) ->
  xs = ("#{key}=#{value}" for key, value of obj when value?)
  xs.join('&')

@fromURLParameter = (str) ->
  obj = {}
  for kv in str.split('&')
    ary = kv.split('=')
    obj[ary[0]] = ary[1]
  obj
