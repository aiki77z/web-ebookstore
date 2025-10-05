import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

/**
 * WebSocket 服务
 * 用于接收订单处理结果的实时推送
 */
class WebSocketService {
    constructor() {
        this.client = null;
        this.connected = false;
        this.subscriptions = new Map();
    }

    /**
     * 连接 WebSocket
     * @param {number} userId - 用户ID
     * @param {function} onOrderResult - 订单结果回调函数
     */
    connect(userId, onOrderResult) {
        if (this.connected) {
            console.log('[WebSocket] 已连接，无需重复连接');
            return;
        }

        // 创建 SockJS 连接
        const socket = new SockJS('http://localhost:8080/ws');

        // 创建 STOMP 客户端
        this.client = new Client({
            webSocketFactory: () => socket,

            // 连接成功回调
            onConnect: (frame) => {
                console.log('[WebSocket] 连接成功:', frame);
                this.connected = true;

                // 订阅用户专属的订单结果队列
                const subscription = this.client.subscribe(
                    `/user/${userId}/queue/order-result`,
                    (message) => {
                        try {
                            const result = JSON.parse(message.body);
                            console.log('[WebSocket] 收到订单结果:', result);

                            // 调用回调函数处理订单结果
                            if (onOrderResult) {
                                onOrderResult(result);
                            }
                        } catch (error) {
                            console.error('[WebSocket] 解析消息失败:', error);
                        }
                    }
                );

                this.subscriptions.set('order-result', subscription);
                console.log(`[WebSocket] 已订阅: /user/${userId}/queue/order-result`);
            },

            // 连接错误回调
            onStompError: (frame) => {
                console.error('[WebSocket] STOMP 错误:', frame);
                this.connected = false;
            },

            // WebSocket 错误回调
            onWebSocketError: (error) => {
                console.error('[WebSocket] WebSocket 错误:', error);
                this.connected = false;
            },

            // 断开连接回调
            onDisconnect: () => {
                console.log('[WebSocket] 已断开连接');
                this.connected = false;
            },

            // 心跳配置
            heartbeatIncoming: 10000,
            heartbeatOutgoing: 10000,

            // 重连配置
            reconnectDelay: 5000,

            // 调试模式
            debug: (str) => {
                console.log('[WebSocket] Debug:', str);
            }
        });

        // 激活连接
        this.client.activate();
    }

    /**
     * 断开 WebSocket 连接
     */
    disconnect() {
        if (this.client && this.connected) {
            // 取消所有订阅
            this.subscriptions.forEach((subscription) => {
                subscription.unsubscribe();
            });
            this.subscriptions.clear();

            // 断开连接
            this.client.deactivate();
            this.connected = false;
            console.log('[WebSocket] 主动断开连接');
        }
    }

    /**
     * 检查连接状态
     */
    isConnected() {
        return this.connected;
    }
}

// 导出单例
export default new WebSocketService();
