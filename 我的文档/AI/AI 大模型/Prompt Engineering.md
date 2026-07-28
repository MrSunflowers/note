# 提示词工程

首先，以目前大模型的发展来看，提示工程仍是一门经验科学，提示词的细微差别可能会导致不一样的输出结果，甚至相同的提示工程技术，在不同模型之间也可能效果会有很大的差异，因此提示工程需要进行大量的实验和测试。尽管如此，编写提示词还是有一些通用的原则可以遵守的。

使用 OpenAI API 进行提示词工程的最佳实践：

https://help.openai.com/zh-hans-cn/articles/6654000-best-practices-for-prompt-engineering-with-the-openai-api

https://www.aneasystone.com/archives/2024/01/prompt-engineering-notes.html

除上述一般技巧外，在以下场景中，针对其特定情况，可以通过以下方式编写提示词

## 对话型 AI

对于对话型 AI，一般可在提示词中提供一些优质对话示例来增强 AI 对于用户问答的确定性，包括引导用户提供必要的上下文信息，这些优质对话往往来自真实的用户交谈记录，再由产品经理或业务人员经过筛选而得到。

例

```python
# prompt_engine.py
 
class CustomerServicePromptEngine:
    def __init__(self, knowledge_base_retriever=None):
        """
        初始化提示词引擎。
        :param knowledge_base_retriever: 一个检索器函数，输入用户问题，返回相关知识点列表。
        """
        self.retriever = knowledge_base_retriever
        # 定义系统角色和核心指令
        self.system_prompt = """你是一个专业的电商客服助手，名字叫小智。你的性格亲切、耐心、专业。
        请严格按照以下要求回答用户问题：
        1. 首先，理解用户意图。用户可能咨询订单、物流、商品、优惠、售后等问题。
        2. 你的回答必须基于提供的【相关知识点】。如果知识点中没有明确信息，请如实告知用户“我暂时没有找到相关信息，建议您联系人工客服”。
        3. 回答要简洁、清晰、直接解决用户问题。避免冗长的客套话，但开头可以说“您好”。
        4. 绝对不要编造信息，不要做出无法兑现的承诺（如具体到货时间、肯定能退款）。
        5. 如果用户问题涉及查询个人订单、退款申请等需要身份验证的操作，引导用户去App内操作或联系人工客服。
        """
 
        # 定义少样本示例，用于引导模型格式和逻辑
        self.few_shot_examples = [
            {
                "user_input": "我昨天买的手机什么时候能发货？",
                "retrieved_knowledge": ["订单支付成功后，24小时内安排发货。发货后一般1-3天送达。"],
                "assistant_response": "您好！订单支付成功后，我们会在24小时内安排发货。发货后，物流通常需要1-3天送达。您可以在订单页面查看最新的物流状态。"
            },
            {
                "user_input": "这个衣服有折扣吗？",
                "retrieved_knowledge": ["当前春装新品享有9折优惠，优惠码：SPRING10。"],
                "assistant_response": "您好！当前春装新品正在做活动，享有9折优惠哦。您可以在结算时使用优惠码【SPRING10】。"
            },
            {
                "user_input": "我要退货，怎么操作？",
                "retrieved_knowledge": ["支持7天无理由退货。请在“我的订单”中找到对应订单，点击“申请售后”，选择退货退款，并填写原因。审核通过后，会提供退货地址。"],
                "assistant_response": "您好！我们支持7天无理由退货。请您打开App，进入“我的订单”，找到需要退货的订单，点击“申请售后”，选择“退货退款”并填写原因。提交后，客服会审核，审核通过后系统会提供退货地址给您。"
            }
        ]
 
    def build_prompt(self, user_input: str, conversation_history: list = None) -> str:
        """
        构建完整的提示词。
        :param user_input: 当前用户输入
        :param conversation_history: 对话历史，格式为 [{"role":"user", "content":"..."}, {"role":"assistant", "content":"..."}, ...]
        :return: 拼接好的完整提示字符串
        """
        # 1. 检索相关知识
        knowledge_snippets = []
        if self.retriever:
            knowledge_snippets = self.retriever(user_input)
        knowledge_str = "\n".join([f"- {ks}" for ks in knowledge_snippets]) if knowledge_snippets else "（暂无相关知识点）"
 
        # 2. 构建上下文历史（简化处理，只取最近3轮）
        history_str = ""
        if conversation_history:
            # 只保留最近几轮，防止token过长
            recent_history = conversation_history[-6:]  # 假设每轮一对对话，取3轮
            for msg in recent_history:
                role = "用户" if msg["role"] == "user" else "客服"
                history_str += f"{role}: {msg['content']}\n"
 
        # 3. 构建少样本示例部分
        example_str = ""
        for example in self.few_shot_examples:
            example_str += f"用户: {example['user_input']}\n"
            example_str += f"【相关知识点】:\n{chr(10).join(['- ' + k for k in example['retrieved_knowledge']])}\n"
            example_str += f"客服: {example['assistant_response']}\n\n"
 
        # 4. 拼接最终提示词（结构化模板）
        full_prompt = f"""
{self.system_prompt}
以下是几个对话示例，请学习回答风格和逻辑：
{example_str}
--------------------
当前对话历史：
{history_str}
--------------------
【相关知识点】：
{knowledge_str}
--------------------
现在，请回答用户的最新问题。
用户: {user_input}
客服: """
        return full_prompt.strip()
 
# 模拟一个简单的检索函数
def mock_retriever(query: str):
    # 这里模拟从向量数据库或ES中检索
    knowledge_base = {
        "发货": ["订单支付成功后，24小时内安排发货。发货后一般1-3天送达。周末仓库休息，可能顺延。"],
        "优惠": ["当前春装新品享有9折优惠，优惠码：SPRING10。", "会员每月可领取一张免邮券。"],
        "退货": ["支持7天无理由退货。请在“我的订单”中申请售后。退货商品需保持完好，吊牌未剪。"],
    }
    for key, value in knowledge_base.items():
        if key in query:
            return value
    return []
 
# 使用示例
if __name__ == "__main__":
    engine = CustomerServicePromptEngine(knowledge_base_retriever=mock_retriever)
    user_query = "我买的春装新品能用优惠券吗？"
    history = [{"role": "user", "content": "你好"}, {"role": "assistant", "content": "您好，我是客服小智，有什么可以帮您？"}]
 
    final_prompt = engine.build_prompt(user_query, history)
    print("生成的完整提示词预览（前500字符）:")
    print(final_prompt[:500], "...")
    # 这里可以将 final_prompt 发送给 OpenAI API 或本地大模型
    # response = openai_chat_completion(final_prompt)
```

其中，优质示例对智能体响应的准确性尤其重要，在真实的项目中，智能体要做的远不止这些，比如用户要咨询离用户最近的门店，这实际上需要后台做大量的工作，来获取用户及门店的位置，并将这些信息集中提供给智能体的提示词中，以达到理想效果。


