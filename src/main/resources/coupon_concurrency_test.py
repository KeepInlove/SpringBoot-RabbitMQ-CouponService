import threading
import requests
import time

# 接口地址
URL = "http://localhost:8080/api/coupon/asyncCoupon"

# 设置并发请求数量（可根据实际情况调整）
THREAD_COUNT = 500  # 超过库存数量，测试并发超卖情况

# 存储结果统计
results = {"success": 0, "failure": 0}

# 请求处理函数
def send_request(thread_id):
    payload = {
        "userId": 1000 + thread_id,  # 模拟不同用户 ID
        "couponId": 4,  # 假设优惠券ID为2
        "num": 1  # 每次请求领取1张
    }
    try:
        response = requests.post(URL, json=payload, timeout=10)
        if response.status_code == 200 and "优惠券领取成功" in response.text:
            results["success"] += 1
        else:
            results["failure"] += 1
        print(f"Thread-{thread_id} - Status: {response.status_code}, Response: {response.text}")
    except requests.exceptions.RequestException as e:
        results["failure"] += 1
        print(f"Thread-{thread_id} - Request failed: {e}")

def main():
    threads = []
    start_time = time.time()

    # 启动线程进行并发请求
    for i in range(THREAD_COUNT):
        t = threading.Thread(target=send_request, args=(i,))
        threads.append(t)
        t.start()

    # 等待所有线程完成
    for t in threads:
        t.join()

    end_time = time.time()

    # 打印测试结果
    print(f"\nTotal requests: {THREAD_COUNT}")
    print(f"Success count: {results['success']}")
    print(f"Failure count: {results['failure']}")
    print(f"Total time cost: {end_time - start_time:.2f} seconds")

if __name__ == "__main__":
    main()
