# 提示词工程

首先，以目前大模型的发展来看，提示工程仍是一门经验科学，提示词的细微差别可能会导致不一样的输出结果，甚至相同的提示工程技术，在不同模型之间也可能效果会有很大的差异，因此提示工程需要进行大量的实验和测试。尽管如此，编写提示词还是有一些通用的原则可以遵守的。

使用 OpenAI API 进行提示词工程的最佳实践：

https://help.openai.com/zh-hans-cn/articles/6654000-best-practices-for-prompt-engineering-with-the-openai-api

https://www.aneasystone.com/archives/2024/01/prompt-engineering-notes.html

## 对话型 AI

对于对话型 AI，一般可在提示词中提供一些优质对话示例来增强 AI 对于用户问答的确定性，包括引导用户提供必要的上下文信息，这些优质对话往往来自真实的用户交谈记录，再由产品经理或业务人员经过筛选而得到。


