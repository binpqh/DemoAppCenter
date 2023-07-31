// updateApp.js
import { Platform } from 'react-native';
import RNFetchBlob from 'rn-fetch-blob';
import { downloadAndInstallApk } from 'react-native-auto-updater';

export async function updateApp() {
  try {
    // Gọi API để lấy đường dẫn file AAB từ Minio
    const response = await fetch('http://your-backend-url/api/aab-url');
    const data = await response.json();

    // Tạo thư mục tạm để lưu trữ tệp AAB
    const downloadDir = Platform.OS === 'ios' ? RNFetchBlob.fs.dirs.DocumentDir : RNFetchBlob.fs.dirs.DownloadDir;
    const tempDir = `${downloadDir}/temp`;
    RNFetchBlob.fs.mkdir(tempDir);

    // Tải và lưu tệp AAB vào thư mục tạm
    const aabPath = `${tempDir}/app.aab`;
    await RNFetchBlob.config({ path: aabPath }).fetch('GET', data.aabUrl);

    // Cài đặt cập nhật từ tệp AAB
    await downloadAndInstallApk(aabPath, {
      preferExternal: true, // Cài đặt ứng dụng vào bộ nhớ ngoài (áp dụng cho Android)
      allowDowngrade: false, // Tắt cài đặt phiên bản cũ hơn nếu có
      showInstallPrompt: true, // Hiển thị cửa sổ xác nhận cài đặt
    });
  } catch (error) {
    console.error('Lỗi khi cập nhật ứng dụng: ', error);
  }
}
