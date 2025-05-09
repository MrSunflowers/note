# 功能分区概览

![c30bbfaf3da6c990761888e7b540d94](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412232245373.png)

Stable Diffusion 作图的基本流程，具体分为以下几个步骤：

1. **模型**：可以选择大模型、LoRA模型、超网络、嵌入式模型等；同时 VAE 也是一种特殊的模型，VAE 就像图片最终的滤镜，有的模型中自带了，就不需要开启 VAE 模型了。
2. **文本**：提供正向提示词和反向提示词。
3. **热图**：提供正向提示词、图生图、局部重绘等。
4. **插件**：可以进行风格、脸部、手部等各种全方位的修改。

这些输入共同作用，通过采样器进行处理。采样器包括CLIP终止层数、随机数种子、采样方法和迭代步数等参数。然后经过 VAE 模型进行图像解码，最终生成所需的图像。

![image-20241223231217014](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412232312192.png)

这张图片展示了Stable Diffusion作图的基本流程，并将其与烹饪过程进行了类比。具体内容如下：

1. **模型=料包**：
   - 大模型=大料包
   - lora模型=小料包
   - 嵌入式=坏掉的部分
   - 模型训练=自制料包

2. **文本=食材**：
   - 正向提示词=好食材
   - 反向提示词=坏食材

3. **热图=样品**：
   - 图生图=整体二次加工
   - 局部重绘=局部二次加工

4. **插件=调整**：
   - 风格、构图、尺寸等
   - 味道、摆盘、大小等

5. **VAE模型=最后一道工序**：
   - VAE模型=浇油

6. **采样器=做菜煲**：
   - CLIP 终止层数=读取做菜要求
   - 随机数种子=做菜指南
   - 采样方法=选择一种做法
   - 迭代步数=做几步完成

![image-20241223231151161](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412232311298.png)

# 采样器

![image-20241223231743907](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412232317975.png)

Stable Diffusion 的作图方式更像是从杂乱的画面中挑选符合我们设置的，将不符合的元素一点点擦除，与人类的作画方式正好相反。

![image-20241223234614584](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412232346682.png)

## CLIP 终止层数

![image-20241223232145328](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412232321541.png)

这张图片展示了不同 CLIP 终止层数（Clip skip）对于生成图像的影响。具体来说：

1. **Clip skip: 1**：最接近提示词，画面表现差强人意。
2. **Clip skip: 3**：提示词与 AI 相互作用，画面表现最好。
3. **Clip skip: 5**：文本描述细节丢失，画面逐渐离谱。
4. **Clip skip: 7**：文本描述细节丢失，画面逐渐离谱。
5. **Clip skip: 9**：与文本描述不相干，画面表现离谱。
6. **Clip skip: 11**：与文本描述不相干，画面表现离谱。

总结：
- 二次元图像：Clip skip 2-3 效果最好。
- 真实图像：Clip skip 1-2 效果最好。
- 因此，Clip skip 2 是最常用的。
- CLIP 值主要影响整体画风，这个数字也是临摹时的重要数据。

## 随机数种子

![image-20241223232541344](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412232325641.png)

随机数种子有两个功能用法

1. 临摹：要实现临摹的效果，则需要保持随机数种子和变异随机数种子保持一致，且变异强度越小越好。
2. 两个随机数种子的融合

上述图片展示了在不同变异强度（Var. strength）下生成图像的变化，以及在不同变异强度下将两个图像融合的结果。

上半部分展示了同一个角色在不同变异强度（从0.0到1.0）下的变化。变异强度越高，图像的变化越大，细节和特征也会有所不同。

下半部分展示了两个图像（一个男孩和一个女孩）的融合结果，分别在不同变异强度（0.1、0.5、0.9）下的效果。随着变异强度的增加，融合图像更倾向于融合图（女孩）的特征。

总结：
1. 随机数种子可以最大限度地保留图像整体特征，是临摹的最重要依据。
2. 变异强度一是调节当前图像的变化强弱。
3. 变异强度二是调节融合图的依照权重，强度越低越能保留当前图特征，强度越高越偏向融合图特征。

从宽度中调整种子和从高度中调整种子功能暂时不稳定，一般不作修改。

## 采样方法

### 采样器分类

1. **传统采样器**
   - Euler, Heun, LMS。
   - 这三个采样器历史悠久，被认为是最简单，但是不太准确的采样器。

2. **祖先采样器，名称中带 a 的**
   - Euler a, DPM2 a, DPM++ 2S a, DPM2 a Karras, DPM++ 2S a Karras。
   - 这些采样器每一步扩散都会加入额外噪声，采样结果很随机，出图不收敛，不容易复现。

3. **官方采样器最初随 sd v1 版本的发布自带的采样器**
   - DDIM, PLMS。
   - DDIM 是第一个采样器，PLMS 则是 DDIM 的替代品，PLMS 快速更快。

4. **DPM 和 DPM++ 系列**
   - DPM2, DPM2 a, DPM++ 2S a, DPM++ 2M, DPM++ SDE, DPM fast, DPM adaptive。
   - DPM 和 DPM++ 系列是2022年发布的采样器，结构相似，但DPM2比DPM 更准确但速度较慢。DPM++ 是对 DPM 的改进，但可能会很慢。

5. **带有 Karras 字样的采样器**
   - LMS Karras, DPM2 Karras, DPM2 a Karras。
   - 这些采样器随着采样步骤的增加，可以减少截断误差。所以说带有 Karras 的采样器一般效果都不错。

6. **UniPC 采样器**
   - UniPC 采样器是2023年发布的新采样器，可在5-10步内实现高质量的出图。速度更快更好。

7. **带有 a 和 SDE 的采样器不收敛**
   - 采样会增大随机噪声，画面效果不稳定，出图多变。

### 采样方法使用总结

1. **万能油 Euler a**
   - 速度快，质量好，但稳定性一般。
   - 步数：15 到 30 步，符合SD默认值20步。
   - 使用建议：因为速度快，很多人使用这个采样器。

2. **二次元效果最好 DPM++ 2M Karras**
   - 速度快，质量好，稳定性高。
   - 步数：20 到 30 步，符合SD默认值20步。
   - 使用建议：被认为是地表最强的采样器之一，目前使用最多。

3. **真实人像效果最好 DPM++ SDE Karras，其次DPM++ SDE**
   - 质量最好，速度慢，稳定性差。
   - 步数：10 到 15 步。
   - 使用建议：因为质量很高，常用于真实人像绘画。

4. **风景效果最好：UniPc，其次DDIM，PtMS**
   - 步数：15 到 25 步。
   - 使用建议：这是一款新款采样器，拥有对比更强、饱和度更高、更锐的特点，适合风景。

**小结：**
1. 稳定的采样器超过30步，没有什么变化，主打稳定；不稳定的采样器，步数高有惊喜，主打高风险高收益。
2. 采样器主要影响：速度、对比度、饱和度、景深等特征。如果色彩不够好看除了另选采样器，也可以另选VAE模型。

## 迭代步数

![image-20241223234421972](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412232344071.png)

这张图片展示了图像去噪的过程，从左到右依次显示了不同迭代步数（Steps）下的效果。具体来说，从左到右分别是1步、6步、11步、16步、21步和26步。

从图片中可以看到，随着迭代步数的增加，图像逐渐从模糊变为清晰，线条从硬朗变为柔和，肢体从怪异变为合理的变化路径。这说明迭代步数越多，图像的去噪效果越好，细节越丰富。

# 模型

## 模型类型及其应用

![image-20241223234927130](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412232349217.png)

这张图片介绍了五种不同的模型及其特点和应用场景：

1. **Stable Diffusion 大模型**
   - 最重要的大模型，基于图像训练、图片容器，提取图片必须的模型，效果最好，常用于控制画风，多个模型调用相当于不同的画师。但模型的文件包都比较大，一般5个G左右，使用起来不够灵活。

2. **VAE 模型**
   - 图像解码器，搭配主模型，对图像进行灰度修复，相当于高饱和度滤镜。部分合并出来的大模型VAE烂掉了，画面会发灰。

3. **T.I.Embedding 嵌入式模型**
   - 负面影响模型，基于关键词训练，一般用于反向提示词，修正人物肢体，适合控制人物角色，但控图能力有限。哩布哩布叫Textual Inversion，是最轻量的模型，一般模型大小只有几十KB。

4. **Hypernetwork 超网络模型**
   - 大多用于控制图像画风，我们可以将其简单理解为低配版的lora，在国内已逐渐被lora所取代，因为它的训练难度很大且应用范围较窄，所以，模型很少。

5. **Lora 模型**
   - 常用于强化角色某个特征，弥补大模型训练慢、不灵活的短板，目前最热门的扩展模型，体积小且控图效果好，文件大小通常在36-144MB。

### 基础算法版本

- V1.5-像素尺寸：512x512dpi
- V2.1-像素尺寸：768x768dpi
- XL1.0-像素尺寸：1024x1024dpi

这张图片提供了一些关于模型下载和使用的相关信息。以下是内容的总结：

### 模型下载

1. https://www.liblib.ai
   - 哇布哇布 国内起步较早的模型网站，模型丰富，可以在线生成图。
2. https://www.esheep.com/
   - 国内新锐模型网站，有作品热榜、站内私信互动等多种创新性功能。
3. https://huggingface.co/tasks
   - 抱脸网，全世界AI研究与模型共享的前沿阵地，专业度强。
4. https://civitai.com
   - C站 全世界最受欢迎的AI绘画模型分享网站。

常见风格：
- 二次元、2.5D、真实系

下载注意：

- 打版图+触发词+生成信息 一起复制

常用后缀：

1. ckpt
2. pt
3. pth
4. safetensors
5. PNG、WEBP图片格式（特殊）

### 模型识别网站

- https://spell.novelai.dev/ 识别模型的类型

安装方法：

- 启动器打开对应的模型文件夹直接丢进去

### 模型显示

1. 打版图的文件夹名与模型保持一致
2. 主模型以及版本

## 大模型

大模型图片的基本风格

只在生成图片与图片融合时使用，其他模型一定和大模型版本一致才可以一起搭配使用

## LoRA 模型

针对大模型生成的图片的某一特征加强，比如细化某个部分的画面，LoRA 模型在点击时以提示词的方式参与运算

## 超网络模型

同 LoRA 模型

## 嵌入式模型

去除某些异常的点，比如残肢

## VAE模型

画面的滤镜风格，运算的最后一步，有的需要有的不需要

## 总结

![image-20241224234024779](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412242340925.png)

这张图片展示了使用Stable Diffusion模型进行图像生成的步骤和方法。以下是具体内容：

### 模型选择
1. **Stable Diffusion模型**
   - **首选大模型**：可以选择不同的模型，如Counterfeit V2.5.safetensors [a074b88]。
   - **主打**：整体风格。

2. **VAE模型**
   - **主打**：画面清晰。

3. **T.I.Embedding嵌入模型**
   - **主打**：修复错误。

4. **Lora模型**
   - **主打**：独立特征。

### 使用方法
- **模型选择区**：注意与大模型对应版本才会显示。
- **混合大模型介入的时机**：可以在0-1之间调整百分比，当前设置为0.8。

### 提示词与触发词
- 在生成图像时，可以在提示词框中添加多个模型触发词。
- **权重值**：范围为0-2，默认值为1，值越大画面越容易失控，有时候可以是负值。

### 示例提示词
```
Dragon,battle,cloud,cloudy sky,glowing,grass,landscape,lens flare,light rays,lightning,monster,no humans,open mouth,outdoors,realistic,sharp teeth
<lora:万字_电影感_龙虎LORAv1.0:0.8>,<lora:绝美华丽脸模型_v1.0:1>
```

### 其他注意事项
- 可以多个模型混合，点选之后会出现现在提示词内，再添加触发词。
- 另外可以修改权重，范围0-2，默认值为1，值越大画面越容易失控，有时候可以是负值。

# 文生图

## 提示词

提示词分为正向提示词和反向提示词

### 提示词语法

- **品质**：质量、相机、光线、渲染器等
- **主语**：主体对象、主要场景
- **细节**：时间、情绪、动作、做什么、色彩、构图等
- **模型**：附加的模型

### 提示词语法注意事项

- 提示词没有绝对的顺序，前面的权重自然高于后面，后面权重也可以随意升降。

![image-20241224234651118](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412242346270.png)

![image-20241224235005680](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412242350882.png)

这张图片展示了SD提示词的玩法和一些符号的使用方法。以下是具体内容：

### 权重符号

- `[提示词]=提示词x0.9倍`
- `{提示词}=提示词x1.05倍`
- `(提示词)=提示词x1.1倍`
- `(提示词:0-1.5)=提示词x0-1.5倍`
- `*` 叠加使用加倍
- `1.5倍之后当心炸图`

### 分隔符号

- `. \ / ! ? % ^ * ; : = \ ~ , 空格 换行`

### 链接符号

- `+`
- `空格 and 空格`

### 迭代符号

- `(狗:猫:0.6) 60%之前是狗，60%之后变猫`

### 示例

图片展示了不同步数（Steps: 1, 4, 8, 12, 16, 20）的生成过程，从一个模糊的图像逐渐变为清晰的猫和狗的图像。

### 小结

1. 稳定的采样器超过30步，没什么变化，主打稳定；不稳定的采样器，步数高有惊喜，主打高风险高收益。
2. 采样器主要影响：速度、对比度、饱和度、景深等特征，如果色彩不够好看除了另选采样器，也可以另选VAE模型。

## 界面介绍

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412252049525.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412252049525.png)

这张图片展示了如何使用Stable Diffusion模型生成图像的界面。以下是一些关键步骤和功能的说明：

1. **正向提示词和反向提示词**：
   - 在“正向提示词”框中输入你想要生成的图像的描述。
   - 在“反向提示词”框中可以输入你不希望出现在图像中的元素。

2. **放大算法模型**：
   - 在“放大算法模型”下拉菜单中选择你想要的放大算法模型，例如Latent、R-ESRGAN 4x+（3D）、R-ESRGAN 4x+Anime6B（二次元）、SwinIR_4x（默认）。

3. **其他参数设置**：
   - 可以调整放大算法模型的放大倍数、提示词权重、重建程度等参数。

4. **生成按钮**：
   - 点击“生成”按钮开始生成图像。

5. **保存和发送图像**：
   - 生成图像后，可以通过界面上的按钮将图像保存到指定文件夹、发送到图生图、发送到后期处理等。

# 图生图

![image-20241225205226235](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412252052425.png)

1. **整体重绘功能**：可以对图像进行整体或局部的重绘。
2. **重绘尺寸调整**：可以调整图像的重绘尺寸，包括宽度和高度。
3. **自动获取图片尺寸**：可以自动获取原始图片的尺寸。
4. **重绘参数设置**：
   - 提示词引导系数（CFG Scale）：值越大提示词作用越大，AI发挥空间越小，建议2-5较为合理。
   - 重绘幅度：值越高重新绘制部分越多，AI发挥空间越大。
   - 增减物体、改风格、修复边缘的参数设置。
5. **重绘尺寸调整**：可以具体设置重绘后的宽度和高度。
6. **图像处理模式**：可以选择仅调整大小、保留原图并裁切、保留原图并填充边缘、打乱重构（拉伸）等模式。

## 整体重绘

![image-20241225205528105](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412252055254.png)

这张图片展示了使用图生图（Image-to-Image）和涂鸦（Sketch-to-Image）两种方法生成图像的效果对比。具体来说：

1. **图生图**：
   - **无提示词**：生成的图像与原始图像没有直接关系，例如从一个海马图案生成了一张人物素描。
   - **反推提示词**：生成的图像与原始图像有一定关联，但不完全一致，例如从一个海马图案生成了一条鱼。

2. **涂鸦**：
   - **无提示词**：生成的图像与原始涂鸦有一定关联，例如从一个海马涂鸦生成了一条鱼。
   - **反推提示词**：生成的图像与原始涂鸦有一定关联，但不完全一致，例如从一个海马涂鸦生成了一条鱼。

总结：
- 图生图更偏向提示词，提示词有引导作用。
- 涂鸦更偏向图像本身，原图的轮廓、色彩有引导作用，提示词反而有干扰。

![image-20241225210047218](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412252100364.png)

这是一张关于图像处理软件界面的截图，主要用于调整图像的生成和局部重绘。以下是一些关键功能和设置的解释：

1. **缩放模式**：
   - 仅调整大小：只调整图像的尺寸，不改变内容。
   - 裁剪后缩放：裁剪图像后再调整大小。
   - 缩放后填充空白：调整大小后填充空白区域。
   - 调整大小（请空间放大）：调整图像大小以填充指定的空间。

2. **蒙版边缘模糊度**：控制蒙版边缘的模糊程度，值越大边缘越柔和。

3. **蒙版透明度**：控制蒙版的透明度，值越大保留越多。

4. **蒙版模式**：
   - 涂抹部分：涂抹指定区域。
   - 涂抹以外的部分：涂抹指定区域外的部分。

5. **重绘蒙版内容**：
   - 重绘蒙版内容：对蒙版内的内容进行重绘。
   - 重绘非蒙版内容：对蒙版外的内容进行重绘。

6. **蒙版区域内容处理**：
   - 填充：保留原图颜色，增加随机噪声。
   - 原版：保留原图的结构和颜色。
   - 潜空间噪声：以原图为基准生成随机噪声。
   - 空白潜空间：没有任何参照，直接生成随机噪声。

7. **重绘区域**：
   - 整张图片：对整张图片进行重绘。
   - 仅蒙版区域：对蒙版区域进行重绘。

8. **脚本**：
   - SD upscale：按选定比例放大图像，使用长/宽拖动条设置分块大小。
   - 分块重绘图像密度：控制重绘密度，值越大边缘融合越好，但原图变化越大。

9. **放大算法**：
   - 无、Lanczos、Nearest、BSRGAN、ESRGAN_4x、LDSR、R-ESRGAN_4x、R-ESRGAN_4x+Anime6B、SwinIR_4x等。

![image-20241225210742567](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412252107741.png)

1. **填充**：
   - **重绘幅度 0.3**：保留原图颜色，叠加随机噪声图，颜色变化不大。适合修手、脸，颜色接近原图。
   - **重绘幅度 0.9**：保留原图的色彩和结构，叠加随机噪声图，保留元素多，颜色变化不大，调高重绘幅度才有变化。适合轻微修改。

2. **原版**：
   - 直接保留原图，不做任何处理。

3. **潜空间噪声**：
   - 以原图为基础生成随机噪声图，叠加随机噪声图，构图变化大，颜色更深。适合深色系。

4. **空白潜空间**：
   - 没有任何参照，直接生成随机噪声图，变化巨大，等同于生图。适合主体随机换底图。

# 插件

![image-20241225212225075](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412252122197.png)

这张图片展示了一些常用的图像生成插件及其功能说明。具体如下：

1. **ADetailer**：脸部修复
2. **Tiled Diffusion**：低显存生成高清大图
3. **Tiled VAE**：预防爆显存，与上面配合使用
4. **ControlNet v1.1.419**：最早最牛的一个插件，与提示词、模型并用，与图生图并列，主打控制
5. **LoRA Block Weight**：未激活
6. **Segment Anything（分离图像元素）**：分割万物

底部的文字解释了插件的多样性和重要性，特别是ControlNet的强大功能：

插件非常多，针对不同的工作需求有不同的插件，因为开源，同一需求就有很多款样式，且各有千秋，核心是看自己需求。ControlNet牛X之处在于，如果我们把插件看成调料、摇盘的话，那ControlNet就是蘸水调料，万物可蘸，万物可改。如果SD没有了ControlNet将黯淡无光，菜鸟与高手的差距就在ControlNet的运用上。

这些插件和功能可以帮助用户更高效地生成和处理图像，满足各种不同的需求。

## ControlNet 

![image-20241225213002656](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412252130799.png)

这是一张关于ControlNet v1.1.419插件界面的介绍图片。以下是图片中各部分的说明：

1. **多个模型处理叠加控制**：
   - 可以设置多个ControlNet单元来处理图像。

2. **一次性处理很多张**：
   - 可以一次性处理多张图像。

3. **放置原图**：
   - 将需要处理的图像放置在此处。

4. **图形处理之后的预览**：
   - 处理后的图像预览。

5. **显卡性能差就开启**：
   - 如果显卡性能较差，可以开启此选项。

6. **预处理模型分类**：
   - 有多种预处理模型可供选择，如Canny边缘检测、深度图、NormalMap等。

7. **预处理模型与其它（提示词+大模型等）的权重**：
   - 可以调整预处理模型与其他提示词和大模型的权重。

8. **发送给ControlNet再次处理**：
   - 处理后的图像可以再次发送给ControlNet进行处理。

9. **设置里修改叠加数量**：
   - 在设置中可以修改叠加数量。

10. **新建画布进行涂鸦**：
    - 可以新建画布进行涂鸦。

11. **用摄像头拍摄图像**：
    - 可以用摄像头拍摄图像。

12. **镜像拍摄图像**：
    - 可以镜像拍摄图像。

13. **将尺寸发送到上面生图设置**：
    - 将图像尺寸发送到上面的生图设置。

14. **预处理模型**：
    - 选择预处理模型，如Canny。

15. **预处理器分辨率**：
    - 预处理器的分辨率设置。

16. **Canny低阈值**：
    - Canny边缘检测的低阈值设置。

17. **Canny高阈值**：
    - Canny边缘检测的高阈值设置。

这些设置可以帮助用户更好地控制图像处理过程，提高图像生成的质量和效果。

![image-20241225213116332](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412252131474.png)

这张图片展示了ControlNet v1.1.419插件的界面，具体来说是模型格式的选择部分。

从图中可以看到：
1. **控制类型**：可以选择不同的预处理器，如Canny（硬边缘）、Depth（深度）、NormalMap（法线贴图）、OpenPose（姿态）、MLSD（直线）等。
2. **预处理器大分类**：可以选择全部或具体的预处理类型。
3. **预处理器细分类**：在选择了预处理类型后，可以进一步选择具体的预处理器，比如canny。
4. **模型与处理器需对应**：在选择预处理器后，需要选择相应的模型。例如，选择canny预处理器后，可以选择`control_v11p_sd15_canny_fp16 [b18e0966]`模型。

此外，图中还提到了模型格式：
- 模型文件_v11版本/正式版，基于fsd1.5开发，模型名称canny_fp16 [b18e0966]
- P正式版
- E测试版
- U未完成

这些信息有助于用户在使用ControlNet插件时选择合适的预处理器和模型，以达到预期的图像处理效果。

![image-20241225214403190](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412252144326.png)


这张图片展示了ControlNet v1.1.419处理器的分类，共分为四个大类：

1. **形状预处理器**
   - Canny（硬边缘）
   - SoftEdge（软边缘）
   - Lineart（线稿）
   - MLSD（直线）
   - Scribble/Sketch（涂鸦/草图）

2. **空间预处理器**
   - Depth（深度）
   - OpenPose（姿态）
   - NormalMap（法线贴图）

3. **风格预处理器**
   - Shuffle（随机洗牌）
   - Reference（参考）
   - Revision（融合）
   - T2I-Adapter（色彩继承）
   - IP-Adapter（风格迁移）

4. **重绘预处理器**
   - Segmentation（语义分割）
   - Tile/Blur（分块/模糊）
   - Inpaint（局部重绘）
   - Instructp2p（指令式变换）
   - Recolor（重上色）

### 形状预处理器

#### Canny（硬边缘）

![image-20241225215550258](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412252155479.png)

#### SoftEdge（软边缘）

![image-20241225215711924](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412252157101.png)

#### Lineart（线稿）

![image-20241225215854304](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202412252158545.png)

#### MLSD（直线）

#### Scribble/Sketch（涂鸦/草图）