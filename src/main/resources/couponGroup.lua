-- 1.参数列表
-- 1.1 优惠券组ID
local couponGroupId = ARGV[1]
-- 1.2 用户ID
local userId = ARGV[2]


-- 2.数据key定义
-- 2.1 优惠券组key
local couponGroupKey = 'CGCouponsAmount:' .. couponGroupId
-- 2.2 领取优惠券key
local userCouponKey = 'UserGotCouponGroup:' .. couponGroupId

-- 3.脚本逻辑
-- 3.1 获取库存

local stock = tonumber(redis.call('get', couponGroupKey)) or 0
if stock <= 0 then
    -- 库存不足
    return 1
end

-- 3.2 检查用户是否已领取
if redis.call('sismember', userCouponKey, userId) == 1 then
    -- 已领取
    return 2
end

-- 3.3 扣减库存并记录用户
redis.call('decr', couponGroupKey)
redis.call('sadd', userCouponKey, userId)

-- 3.4 发送优惠券消息到stream流
-- redis.call('xadd', 'stream.coupon', '*', 'couponGroupId', couponGroupId, 'userId', userId)
-- 成功
return 0