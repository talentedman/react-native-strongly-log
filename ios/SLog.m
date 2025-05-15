
#import "SLog.h"
#import "SLFileUtils.h"


@implementation SLog

static BOOL IS_LOG = YES;
static BOOL IS_CONSOLE = YES;

static NSString* sm_logFolderPath;
static NSString* sm_logFile;
static FILE* file = NULL;


#define SLog_TAG @"ReactNative"

+(NSString*)getNowTime {
    NSDateFormatter *fmt = [[NSDateFormatter alloc]init];
    fmt.dateFormat = @"yyyy-MM-dd-HH.mm.ss";
    return [fmt stringFromDate:[NSDate new]];
}

+(BOOL)doCreateLogFile {
    if (!sm_logFolderPath) return NO;
    
    [SLFileUtils mkdirs:sm_logFolderPath];
    
    
    NSString* timeStr = [SLog getNowTime];
    sm_logFile = [sm_logFolderPath stringByAppendingPathComponent:[NSString stringWithFormat:@"/access_%@.log", timeStr]];
    
    file = fopen([sm_logFile UTF8String], "a+");
    if (!file) {
        NSLog(@"create log");
        return NO;
    }
    
    return YES;
}

+(void)closeLogFile {
    if (!file) return;
    
    fclose(file);
    file = NULL;
    sm_logFile = nil;
}

+(void)initLogFile:(NSString*)logFileFolder {
    [SLog closeLogFile];
    if (!logFileFolder || logFileFolder.length == 0) {
        sm_logFolderPath = nil;
        return;
    }
    
    sm_logFolderPath = logFileFolder;
    
    [SLog doCreateLogFile];
}

+(void)logFile:(NSString*)level tag:(NSString*)tag msg:(NSString*)msg {
    NSString* buffer = [NSString stringWithFormat:@"%@ %@ %@ %@\n"
                        ,[SLog getNowTime]
                        ,level
                        ,tag
                        ,msg];
    const char* str = [buffer UTF8String];
    unsigned long l = strlen(str);
    
    @synchronized (SLog.class) {
        if (!file) {
            if (![SLog doCreateLogFile]) {
                return;
            }
        }
        
        fwrite(str, l, 1, file);
    }
}


#pragma mark - setup

+ (void)setup {
    NSString* logFileFolder = [SLog getLogFileFolder];
    BOOL bLog = YES;
    BOOL bConsole = YES;
    
    IS_LOG = bLog;
    IS_CONSOLE = bConsole;
    [SLog initLogFile:logFileFolder];
    
    [SLog clearOldLogsInIOThread];
}

#pragma mark - logPath

+(NSString*)getLogFileFolder {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString* folder = [paths firstObject];
    return [folder stringByAppendingPathComponent:@"rnsl_logs"];
}
+(NSString*)getLogZipFolder {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString* folder = [paths firstObject];
    return [folder stringByAppendingPathComponent:@"rnsl_logs.zip"];
}
+(NSString*)getLogFile {
    return sm_logFile;
}

#pragma mark - clearOldLogs

+(void)clearOldLogs {
    NSString* logFileFolder = [SLog getLogFileFolder];
    
    NSFileManager* fm = [NSFileManager defaultManager];
    
    NSTimeInterval now = [[NSDate date] timeIntervalSince1970];
    
    NSArray<NSString*>* files = [SLFileUtils listFiles:fm folderPath:logFileFolder];
    for (NSString* file in files) {
      BOOL isDirectory;
      [fm fileExistsAtPath:file isDirectory:&isDirectory];
      if (isDirectory) continue;
      
      //获取文件的属性词典
      NSDictionary *attr =[fm attributesOfItemAtPath:file error:nil];
      if (!attr) continue;
      //获取文件的创建时间
//      NSDate *createDate = [attr objectForKey:NSFileCreationDate];
      //获取文件的修改时间
      NSDate *modifiedDate = [attr objectForKey:NSFileModificationDate];
      
      int days = ((now - [modifiedDate timeIntervalSince1970]) / (3600 * 24));
      if (days < 4) continue;
      
      [SLFileUtils deleteFile:fm path:file];
    }
}

+(void)clearOldLogsInIOThread {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
      [SLog clearOldLogs];
    });
}


#pragma mark - verbose

+(void)v:(NSString*)msg {
    if (!IS_LOG) return;
    [SLog v:SLog_TAG msg:msg];
}
+(void)v:(NSString*)tag msg:(NSString*)msg {
    if (!IS_LOG) return;
    
    if (IS_CONSOLE) {
        NSLog(@"V/%@: %@", tag, msg);
    }
    
    if (sm_logFolderPath) {
        [SLog logFile:@"V" tag:tag msg:msg];
    }
}

#pragma mark - debug

+(void)d:(NSString*)msg {
    if (!IS_LOG) return;
    [SLog d:SLog_TAG msg:msg];
}
+(void)d:(NSString*)tag msg:(NSString*)msg {
    if (!IS_LOG) return;
    
    if (IS_CONSOLE) {
        NSLog(@"D/%@: %@", tag, msg);
    }
    
    if (sm_logFolderPath) {
        [SLog logFile:@"D" tag:tag msg:msg];
    }
}

#pragma mark - info

+(void)i:(NSString*)msg {
    if (!IS_LOG) return;
    [SLog i:SLog_TAG msg:msg];
}
+(void)i:(NSString*)tag msg:(NSString*)msg {
    if (!IS_LOG) return;
    
    if (IS_CONSOLE) {
        NSLog(@"I/%@: %@", tag, msg);
    }
    
    if (sm_logFolderPath) {
        [SLog logFile:@"I" tag:tag msg:msg];
    }
}

#pragma mark - warn

+(void)w:(NSString*)msg {
    if (!IS_LOG) return;
    [SLog w:SLog_TAG msg:msg];
}
+(void)w:(NSString*)tag msg:(NSString*)msg {
    if (!IS_LOG) return;
    
    if (IS_CONSOLE) {
        NSLog(@"W/%@: %@", tag, msg);
    }
    
    if (sm_logFolderPath) {
        [SLog logFile:@"W" tag:tag msg:msg];
    }
}

#pragma mark - error

+(void)e:(NSString*)msg {
    if (!IS_LOG) return;
    [SLog e:SLog_TAG msg:msg];
}
+(void)e:(NSString*)tag msg:(NSString*)msg {
    if (!IS_LOG) return;
    
    if (IS_CONSOLE) {
        NSLog(@"E/%@: %@", tag, msg);
    }
    
    if (sm_logFolderPath) {
        [SLog logFile:@"E" tag:tag msg:msg];
    }
}

@end
