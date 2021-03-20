for i=1, #KEYS do
    if redis.call("EXISTS",KEYS[i]) == 0 then
        return false
    else
        local stock= redis.call('get',KEYS[i])
        if  stock - ARGV[i] <0  then
            return false
        end
    end
end
for i=1, #KEYS
do
    local stock= redis.call('get',KEYS[i])
    redis.call('set', KEYS[i], stock-ARGV[i])
end
return true