# 开发环境搭建

## CUDA 安装

CUDA (Compute Unified Device Architecture) 是由 NVIDIA 开发的一种并行计算平台和编程模型。它允许开发人员利用 NVIDIA 的图形处理器 (GPU) 来加速各种计算任务，包括科学计算、数据分析、深度学习和机器学习等。

CUDA 提供了一套扩展的 C/C++ 编程接口，使开发人员能够编写并行计算代码，将任务分配给 GPU 上的多个并行处理单元（称为CUDA核心），从而实现加速计算。通过利用 GPU 的高并行处理能力和内存带宽，CUDA 可以显著提高计算密集型任务的性能。

除了编程接口，CUDA 还包括了一个运行时系统和驱动程序，这些组件协同工作，将编写的 CUDA 代码转化为可以在 GPU 上执行的指令。CUDA 运行时系统提供了 GPU 管理和调度任务的功能，而驱动程序则负责与 GPU 通信并将指令传送到 GPU 上执行。

CUDA 不仅在科学计算领域得到广泛应用，还成为深度学习和机器学习的主要工具之一。深度学习框架如 TensorFlow、PyTorch 和 Caffe 等都支持使用 CUDA 进行 GPU 加速，从而加快神经网络的训练和推断过程。

需要注意的是，CUDA 只在支持 NVIDIA GPU 的系统上有效，因此它是针对 NVIDIA 硬件的特定技术。

[下载官网](https://developer.nvidia.com/cuda-downloads)

[CUDA 版本和驱动对应关系](https://docs.nvidia.com/cuda/cuda-toolkit-release-notes/index.html)

[CUDA 安装包地址](https://developer.nvidia.com/cuda-toolkit-archive)

根据当前显卡驱动的版本来选择 cuda 版本的安装

![image-20230630225452501](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202306302254587.png)

![image-20230630225736192](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202306302257276.png)

这里第一次安装，跟[视频](https://www.bilibili.com/video/BV14R4y1g7qs/?vd_source=02d73fa6f895845af215b9007452e039)保持一致

选择 [CUDA Toolkit 11.1.0](https://developer.nvidia.com/cuda-11.1.0-download-archive)

![image-20230630230124234](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202306302301309.png)

双击安装，第一步是选择临时文件存放地址，然后一直下一步

配置环境变量 C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v11.1\bin 到 path

完成安装：完成安装过程后，可以验证 CUDA 是否正确安装。打开命令提示符或 PowerShell，输入 "nvcc -V" 命令，它将显示 CUDA 版本和安装信息。

使用 nvidia-smi 查看显卡情况

```shell
nvidia-smi
```

## 安装 ANACONDA

ANACONDA 是 Python 包的管理器，Anaconda 支持 Windows、macOS 和 Linux。

[Anaconda](https://www.anaconda.com/) 下载 windows 版本

添加环境变量到 path

```
D:\Sowftware\anaconda3\condabin
D:\Sowftware\anaconda3\Scripts
D:\Sowftware\anaconda3
```

使用命令检测

```
conda env list
```

[修改镜像地址](https://mirror.tuna.tsinghua.edu.cn/help/anaconda/)

修改用户目录下的 `.condarc` 文件来使用 TUNA 镜像源

Anaconda 是一个用于科学计算的 Python 发行版，支持 Linux, Mac, Windows, 包含了众多流行的科学计算、数据分析的 Python 包。

Anaconda 安装包可以到 https://mirrors.tuna.tsinghua.edu.cn/anaconda/archive/ 下载。

TUNA 还提供了 Anaconda 仓库与第三方源（conda-forge、msys2、pytorch等，[查看完整列表](https://mirrors.tuna.tsinghua.edu.cn/anaconda/cloud/)，更多第三方源可以前往[校园网联合镜像站](https://mirrors.cernet.edu.cn/list/anaconda)查看）的镜像，各系统都可以通过修改用户目录下的 `.condarc` 文件来使用 TUNA 镜像源。**Windows 用户无法直接创建名为 `.condarc` 的文件，可先执行 `conda config --set show_channel_urls yes` 生成该文件之后再修改**。

创建一个虚拟环境

```
conda create -n jackcui jupyter notebook cudnn
```

jackcui 是虚拟环境的名称 可以随便起

使用 activate jackcui 命令切换到 jackcui 环境，各个开发环境互不影响，这样就可以在一台电脑上，配置多个开发环境，避免冲突问题

```
activate jackcui
```

在 Anaconda 中，虚拟环境默认安装到 Anaconda 的安装目录下的 `envs` 文件夹中。具体而言，虚拟环境的默认安装路径是：

Windows 系统：`C:\ProgramData\Anaconda3\envs\` macOS 和 Linux 系统：`/opt/anaconda3/envs/`

在这些路径下，每个创建的虚拟环境将被放置在一个单独的文件夹中，该文件夹的名称对应于虚拟环境的名称。例如，如果你创建了一个名为 "myenv" 的虚拟环境，它将被安装在 `envs` 文件夹下的 `myenv` 文件夹中。

请注意，虚拟环境的默认安装路径是可配置的。在创建虚拟环境时，你可以指定一个自定义的路径。如果你希望将虚拟环境安装到不同的位置，可以在创建虚拟环境时提供自定义路径。

至此 Anaconda 配置完成

## 安装AI绘图环境

[git clone 项目](https://github.com/AUTOMATIC1111/stable-diffusion-webui)

使用 conda 创建一个 python 3.10.6 的虚拟环境

```
conda create -n novelai python==3.10.6
```

激活环境

```
conda activate novelai
```

查看项目中的 requirements.txt 文件，里面是详细的依赖

安装 [pytorch](https://pytorch.org/get-started/locally/)

根据环境选择安装命令

```
conda install pytorch torchvision torchaudio pytorch-cuda=11.6 -c pytorch -c nvidia
```

进入项目路径

![image-20230701080656334](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202307010806385.png)

使用 pip 指令安装依赖

```
python -m pip install -r requirements.txt
```

下载权重文件

```
https://pan.baidu.com/s/1ADjKFoXqBEXRR_hHbOddHg?pwd=o89H
```

解压

再拷贝 .cache 目录下的所有文件到用户目录的 .cache 目录

其余文件夹拷贝到项目根目录

运行代码

```
python launch.py
```