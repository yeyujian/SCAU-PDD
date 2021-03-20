if redis.call("EXISTS",KEYS[1]) == 1 then
    local stock= 0
    stock=redis.call('get',KEYS[1])
    if  stock - ARGV[1] >0  then
        redis.call('set', KEYS[1], stock-ARGV[1])
        return true
    end
end
return false