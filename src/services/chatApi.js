// Chat API service for n8n agent
const CHAT_API_URL = process.env.REACT_APP_CHAT_API_URL || 'http://localhost:5678/webhook/chat';

/**
 * Send a message to the chat agent
 * @param {string} message - User message
 * @param {Array} history - Conversation history
 * @returns {Promise<Object>} Response from the agent
 */
export async function sendChatMessage(message, history = []) {
  try {
    const response = await fetch(CHAT_API_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        message,
        history,
      }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Chat API request failed:', error);
    throw error;
  }
}


